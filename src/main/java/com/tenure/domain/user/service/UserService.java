package com.tenure.domain.user.service;

import com.tenure.domain.user.dto.request.SignupRequest;
import com.tenure.domain.user.dto.response.SignupResponse;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.exception.UserErrorCode;
import com.tenure.domain.user.repository.UserRepository;
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


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    // 회원가입
    // (검증 -> 암호화 -> 저장)을 하나의 트랜잭션으로 처리
    @Transactional
    public SignupResponse signup(SignupRequest request) {

        // 1) 비밀번호 확인 일치 검사
        if (!request.password().equals(request.passwordConfirm())) {
            throw new CustomException(UserErrorCode.PASSWORD_MISMATCH);
        }

        // 2) 이메일 중복 검사
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 3) 닉네임 중복 검사
        if (userRepository.existsByUsername(request.username())) {
            throw new CustomException(UserErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // 4) User 생성 (비밀번호 BCrypt 암호화)
        User user = User.createByEmail(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.username(),
                request.gender(),
                request.heightCm(),
                request.weightKg(),
                null
        );

        // 5) 저장
        User savedUser = userRepository.save(user);
        log.info("회원가입 완료: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        // 6) 응답 DTO 변환
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

    // 내 정보 조회.
    // currentUserId(JWT에서 추출된 로그인 사용자 ID)로 조회
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        return UserProfileResponse.from(user);
    }
}