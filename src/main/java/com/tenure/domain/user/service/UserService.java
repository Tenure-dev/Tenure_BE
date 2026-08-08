package com.tenure.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.repository.PurchaseIntentRepository;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.user.dto.request.AccountSettingsUpdateRequest;
import com.tenure.domain.user.dto.request.SignupRequest;
import com.tenure.domain.user.dto.request.UserReportCreateRequest;
import com.tenure.domain.user.dto.response.BlockedUserResponse;
import com.tenure.domain.user.dto.response.SignupResponse;
import com.tenure.domain.user.dto.response.UserReportCreateResponse;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.entity.UserBlock;
import com.tenure.domain.user.entity.UserReport;
import com.tenure.domain.user.repository.UserReportRepository;
import com.tenure.global.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.tenure.domain.user.exception.UserErrorCode;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.domain.user.dto.request.WithdrawalRequest;
import com.tenure.domain.user.entity.UserWithdrawal;
import com.tenure.domain.user.repository.UserWithdrawalRepository;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import com.tenure.global.storage.ImageDeletionService;
import com.tenure.global.storage.ImageStorageService;
import com.tenure.global.storage.validation.ImageValidator;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tenure.domain.user.dto.request.LoginRequest;
import com.tenure.domain.user.dto.response.TokenResponse;
import com.tenure.global.security.JwtProvider;
import com.tenure.domain.user.dto.response.UserProfileResponse;
import com.tenure.domain.user.dto.request.ProfileUpdateRequest;
import com.tenure.domain.user.dto.SettlementAccountDto;
import com.tenure.global.util.AesEncryptor;
import com.tenure.domain.user.dto.response.PublicUserProfileResponse;
import com.tenure.domain.user.repository.UserBlockRepository;
import com.tenure.domain.auth.service.EmailVerificationStore;
import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.address.repository.DeliveryAddressRepository;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final AesEncryptor aesEncryptor;
    private final UserBlockRepository userBlockRepository;
    private final EmailVerificationStore verificationStore;
    private final DeliveryAddressRepository addressRepository;
    private final UserWithdrawalRepository userWithdrawalRepository;
    private final ImageStorageService imageStorageService;
    private final FollowRelationshipRepository followRepository;
    private final PurchaseIntentRepository purchaseIntentRepository;
    private final PurchaseOfferRepository purchaseOfferRepository;
    private final UserReportRepository userReportRepository;
    private final ItemRepository itemRepository;
    private final OotdRepository ootdRepository;
    private final ProductRepository productRepository;
    private final ImageDeletionService imageDeletionService;
    private final ImageValidator imageValidator;


    // 회원가입
    @Transactional
    public SignupResponse signup(SignupRequest request) {

        // 1) 비밀번호 확인 일치 검사
        if (!request.password().equals(request.passwordConfirm())) {
            throw new CustomException(UserErrorCode.PASSWORD_MISMATCH);
        }

        // 2) 이메일 인증 완료 여부 검증
        //    (app.email-verification-enabled=false 이면 시연 기간 임시로 건너뜀)
        if (emailVerificationEnabled && !verificationStore.isVerified(request.email())) {
            throw new CustomException(UserErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 3) 이메일 중복 검사
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 4) 닉네임 중복 검사
        if (userRepository.existsByUsername(request.username())) {
            throw new CustomException(UserErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // 5) User 생성 (비밀번호 BCrypt 암호화)
        User user = User.createByEmail(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.username(),
                request.gender(),
                request.heightCm(),
                request.weightKg(),
                request.profileImageUrl()   // 선택값, 없으면 null
        );
        user.updateProfileImageMetadata(request.profileImageUrl(), resolveObjectKey(request.profileImageUrl()));
        User savedUser = userRepository.save(user);

        // 6) 온보딩 주소를 첫 배송지이자 기본 배송지로 등록
        DeliveryAddress address = DeliveryAddress.create(
                savedUser,
                savedUser.getUsername(),   // receiverName: 우선 닉네임 사용
                "",                        // phone: 온보딩에서 안 받음
                request.addressLine1(),
                request.addressLine2(),
                request.postalCode(),
                null,                      // requestNote
                true                       // isDefault: 첫 배송지이므로 기본
        );
        addressRepository.save(address);

        // 7) 인증 정보 정리 (이메일 인증을 사용한 경우에만)
        if (emailVerificationEnabled) {
            verificationStore.remove(request.email());
        }

        log.info("회원가입 완료: userId={}, email={}", savedUser.getId(), savedUser.getEmail());
        return SignupResponse.from(savedUser);
    }

    // 로그인
    // 이메일로 사용자를 찾고 비밀번호를 검증한 뒤 Access Token 발급
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {

        // 1) 이메일로 사용자 조회. 없으면 로그인 실패
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(UserErrorCode.LOGIN_FAILED));

        // 2) 비밀번호 검증. 평문(request) vs 저장된 해시(user) 비교
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new CustomException(UserErrorCode.LOGIN_FAILED);
        }

        // 탈퇴한 회원은 로그인 불가
        if (user.isWithdrawn()) {
            throw new CustomException(UserErrorCode.LOGIN_FAILED);
        }

        // 3) Access Token 발급 (subject에 userId 담김)
        String accessToken = jwtProvider.createAccessToken(user.getId());
        log.info("로그인 성공: userId={}, email={}", user.getId(), user.getEmail());

        // 4) 응답
        return new TokenResponse(accessToken, user.getId(), user.getUsername());
    }

    // 내 정보 조회
    // currentUserId(JWT에서 추출된 로그인 사용자 ID)로 조회
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        return UserProfileResponse.from(user);
    }

    // 프로필 수정
    @Transactional
    public UserProfileResponse updateMyProfile(Long currentUserId, ProfileUpdateRequest request) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        String previousProfileImageObjectKey = user.getProfileImageObjectKey();

        // 닉네임을 바꾸려는 경우에만 중복 검사.
        // 현재 자기 닉네임과 다르고, 그 닉네임을 이미 누군가 쓰고 있으면 에러
        if (request.username() != null
                && !request.username().equals(user.getUsername())
                && userRepository.existsByUsername(request.username())) {
            throw new CustomException(UserErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // 엔티티의 수정 메서드 호출. 트랜잭션 종료 시 변경 감지로 자동 UPDATE
        user.updateProfile(
                request.username(),
                request.gender(),
                request.heightCm(),
                request.weightKg(),
                request.profileImageUrl()
        );
        if (request.profileImageUrl() != null) {
            String newProfileImageObjectKey = resolveObjectKey(request.profileImageUrl());
            user.updateProfileImageMetadata(request.profileImageUrl(), newProfileImageObjectKey);
            deleteIfChanged(previousProfileImageObjectKey, newProfileImageObjectKey);
        }

        return UserProfileResponse.from(user);
    }

    // 계정 설정 수정
    // 정산 계좌의 계좌번호는 AES로 암호화한 뒤 JSON으로 직렬화해 저장
    @Transactional
    public UserProfileResponse updateAccountSettings(
            Long currentUserId,
            AccountSettingsUpdateRequest request
    ) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        String settlementAccountJson = null;
        if (request.settlementAccount() != null) {
            SettlementAccountDto account = request.settlementAccount();

            // 계좌번호만 암호화. 은행명·예금주는 평문 유지
            String encryptedAccountNumber = account.accountNumber() != null
                    ? aesEncryptor.encrypt(account.accountNumber())
                    : null;

            // 암호화된 계좌번호로 새 DTO를 만들어 JSON 직렬화
            SettlementAccountDto encryptedAccount = new SettlementAccountDto(
                    account.bankName(),
                    encryptedAccountNumber,
                    account.accountHolder()
            );

            try {
                settlementAccountJson = objectMapper.writeValueAsString(encryptedAccount);
            } catch (JsonProcessingException e) {
                throw new CustomException(CommonErrorCode.INVALID_REQUEST);
            }
        }

        user.updateAccountSettings(request.defaultShippingFee(), settlementAccountJson);

        return UserProfileResponse.from(user);
    }

    // 타 사용자 프로필 조회
    // 내가 상대를 차단했거나, 상대가 나를 차단한 경우 조회를 막는다.
    @Transactional(readOnly = true)
    public PublicUserProfileResponse getUserProfile(Long currentUserId, Long targetUserId) {
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 탈퇴 회원 조회 차단
        if (target.isWithdrawn()) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        // 차단 관계 검증 (양방향)
        boolean blockedByMe = userBlockRepository.isBlocked(currentUserId, targetUserId);
        boolean blockedByTarget = userBlockRepository.isBlocked(targetUserId, currentUserId);
        if (blockedByMe || blockedByTarget) {
            throw new CustomException(UserErrorCode.USER_BLOCKED);
        }

        // 프로필 헤더 카운트 조회
        long feedCount = ootdRepository.countByOwner_IdAndPublicationStatus(
                targetUserId, OotdPublicationStatus.ACTIVE);
        long itemCount = itemRepository.countByOwner_Id(targetUserId);
        long followerCount = followRepository.countByFollowing_IdAndStatus(
                targetUserId, FollowStatus.ACCEPTED);
        boolean isFollowing = followRepository.existsByFollower_IdAndFollowing_IdAndStatus(
                currentUserId, targetUserId, FollowStatus.ACCEPTED);

        return PublicUserProfileResponse.of(target, feedCount, itemCount, followerCount, isFollowing);
    }

    // 회원 탈퇴
    @Transactional
    public void withdraw(Long currentUserId, WithdrawalRequest request) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 이미 탈퇴한 회원인지 확인 (중복 탈퇴 방지)
        if (user.isWithdrawn()) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }
        List<String> imageObjectKeys = collectOwnedImageObjectKeys(user);

        // 1) 탈퇴 사유 기록
        userWithdrawalRepository.save(UserWithdrawal.create(currentUserId, request.reason()));

        // 2) User 익명화 (변경 감지로 자동 UPDATE)
        user.withdraw();
        imageObjectKeys.stream()
                .distinct()
                .forEach(imageDeletionService::deleteAfterCommit);

        log.info("회원 탈퇴 완료: userId={}, reason={}", currentUserId, request.reason());
    }

    // 프로필 이미지 업로드
    public String uploadProfileImage(MultipartFile image) {
        imageValidator.validateGeneralImage(image);
        // 파일 검증
        if (image == null || image.isEmpty()) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
        // 이미지 형식만 허용
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }

        // "profile" 디렉토리에 저장하고 URL 반환
        return imageStorageService.store(image, "profile");
    }

    private List<String> collectOwnedImageObjectKeys(User user) {
        Long userId = user.getId();
        List<String> imageObjectKeys = new ArrayList<>();
        if (user.getProfileImageObjectKey() != null) {
            imageObjectKeys.add(user.getProfileImageObjectKey());
        }
        imageObjectKeys.addAll(itemRepository.findRepresentativeImageObjectKeysByOwnerId(userId));
        imageObjectKeys.addAll(ootdRepository.findImageObjectKeysByOwnerId(userId));
        imageObjectKeys.addAll(productRepository.findMainImageObjectKeysBySellerId(userId));
        return imageObjectKeys;
    }

    private void deleteIfChanged(String previousObjectKey, String nextObjectKey) {
        if (previousObjectKey != null && !previousObjectKey.equals(nextObjectKey)) {
            imageDeletionService.deleteAfterCommit(previousObjectKey);
        }
    }

    private String resolveObjectKey(String imageUrl) {
        return imageStorageService.objectKeyFromUrl(imageUrl).orElse(null);
    }

    @Transactional
    public void userBlock(Long currentUserId, Long targetUserId) {
        // 1) 자기 자신 차단 불가
        if (currentUserId.equals(targetUserId)) {
            throw new CustomException(UserErrorCode.CANNOT_BLOCK_SELF);
        }

        // 2) 대상 사용자 존재 확인
        User target = userRepository.findById(targetUserId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 3) 이미 차단한 경우 거부 (unique 제약 위반 방지)
        if (userBlockRepository.isBlocked(currentUserId, targetUserId)) {
            throw new CustomException(UserErrorCode.ALREADY_BLOCKED);
        }

        // 4) 차단 관계 생성
        User blocker = userRepository.findById(currentUserId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        userBlockRepository.save(UserBlock.create(blocker, target));

        // 5) 양방향 팔로우 즉시 해제
        followRepository.findByFollower_IdAndFollowing_Id(currentUserId, targetUserId)
            .ifPresent(followRepository::delete);
        followRepository.findByFollower_IdAndFollowing_Id(targetUserId, currentUserId)
            .ifPresent(followRepository::delete);

        // 6) 두 사용자 사이의 대기 중(SENT) 구매의사 취소 + 승인 해제
        purchaseIntentRepository.findSentBetweenUsersForUpdate(
            currentUserId, targetUserId, PurchaseIntentStatus.SENT
        ).forEach(PurchaseIntent::cancelAndReleaseAuthorization);

        // 7) 두 사용자 사이의 대기 중(SENT) 구매제안 취소 + 승인 해제
        purchaseOfferRepository.findSentBetweenUsersForUpdate(
            currentUserId, targetUserId, PurchaseOfferStatus.SENT
        ).forEach(PurchaseOffer::cancelAndReleaseAuthorization);

        log.info("사용자 차단: {} -> {}", currentUserId, targetUserId);
    }

    @Transactional
    public void userUnblock(Long currentUserId, Long targetUserId) {
        // 대상 사용자 존재 확인
        if (!userRepository.existsById(targetUserId)) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        // 차단 관계 조회. 없으면 404
        UserBlock userBlock = userBlockRepository.findByBlocker_IdAndBlocked_Id(currentUserId, targetUserId)
            .orElseThrow(() -> new CustomException(UserErrorCode.BLOCK_NOT_FOUND));

        userBlockRepository.delete(userBlock);
        log.info("사용자 차단 해제: {} -> {}", currentUserId, targetUserId);
    }

    // 내가 차단한 사용자 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<BlockedUserResponse> getBlockedUsers(Long currentUserId, Pageable pageable) {
        Page<UserBlock> blocks = userBlockRepository.findByBlockerId(currentUserId, pageable);
        return PageResponse.from(blocks, ub -> BlockedUserResponse.of(ub.getBlocked(), ub.getCreatedAt()));
    }

    // 사용자 신고
    @Transactional
    public UserReportCreateResponse reportUser(Long currentUserId, Long targetUserId, UserReportCreateRequest request) {

        // 1) 자기 자신 신고 불가
        if (currentUserId.equals(targetUserId)) {
            throw new CustomException(UserErrorCode.CANNOT_REPORT_SELF);
        }

        // 2) 신고 대상 사용자 존재 확인
        User reported = userRepository.findById(targetUserId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 3) 같은 신고자 + 같은 대상 중복 신고 확인
        userReportRepository.findByReporter_IdAndReported_Id(currentUserId, targetUserId)
            .ifPresent(existing -> {
                throw new CustomException(UserErrorCode.ALREADY_REPORTED, existing.getId());
            });

        // 4) 신고 저장
        User reporter = userRepository.findById(currentUserId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        UserReport report = UserReport.create(reporter, reported, request.reasonType(), request.reasonDetail());
        UserReport saved = userReportRepository.save(report);

        log.info("사용자 신고: {} -> {}", currentUserId, targetUserId);
        return UserReportCreateResponse.from(saved);
    }

    @org.springframework.beans.factory.annotation.Value("${app.email-verification-enabled:true}")
    private boolean emailVerificationEnabled;
}
