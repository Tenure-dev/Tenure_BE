package com.tenure.domain.auth.service;

import com.tenure.domain.auth.exception.AuthErrorCode;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// 인증 로직
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final EmailVerificationStore verificationStore;

    /**
     * 이메일 인증번호 발송.
     * 이미 가입된 이메일이면 에러. 6자리 랜덤 번호를 생성해 저장하고 메일 발송.
     */
    public void sendEmailVerification(String email) {
        // 이미 가입된 이메일인지 확인
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 6자리 랜덤 인증번호 생성 (100000 ~ 999999)
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);

        verificationStore.save(email, code);
        emailService.sendVerificationCode(email, code);
    }
}