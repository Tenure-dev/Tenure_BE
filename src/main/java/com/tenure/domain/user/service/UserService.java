package com.tenure.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenure.domain.user.dto.request.AccountSettingsUpdateRequest;
import com.tenure.domain.user.dto.request.SignupRequest;
import com.tenure.domain.user.dto.response.SignupResponse;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.exception.UserErrorCode;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
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

    // 회원가입
    @Transactional
    public SignupResponse signup(SignupRequest request) {

        // 1) 비밀번호 확인 일치 검사
        if (!request.password().equals(request.passwordConfirm())) {
            throw new CustomException(UserErrorCode.PASSWORD_MISMATCH);
        }

        // 2) 이메일 인증 완료 여부 검증
        if (!verificationStore.isVerified(request.email())) {
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

        // 7) 인증 정보 정리 (가입 완료됐으므로 저장소에서 제거)
        verificationStore.remove(request.email());

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

        // 대상 사용자 조회
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 차단 관계 확인
        // 내가 상대를 차단했거나, 상대가 나를 차단한 경우 -> 차단 에러
        boolean blockedByMe = userBlockRepository.isBlocked(currentUserId, targetUserId);
        boolean blockedByTarget = userBlockRepository.isBlocked(targetUserId, currentUserId);
        if (blockedByMe || blockedByTarget) {
            throw new CustomException(UserErrorCode.USER_BLOCKED);
        }

        return PublicUserProfileResponse.from(target);
    }
}