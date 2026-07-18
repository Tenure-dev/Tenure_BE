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


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 이메일 회원가입
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
}