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

    /**
     * 이메일 인증번호 확인.
     * 저장된 번호와 일치하고 만료 안 됐으면 인증 완료 처리. 아니면 에러.
     * (화면: 일치 -> "인증되었습니다", 불일치 -> "인증번호가 틀렸습니다")
     */
    public void verifyEmailCode(String email, String code) {
        boolean verified = verificationStore.verify(email, code);
        if (!verified) {
            throw new CustomException(AuthErrorCode.EMAIL_VERIFICATION_FAILED);
        }
    }

    /**
     * 닉네임 중복 확인.
     * 이미 사용 중이면 available=false, 사용 가능하면 true.
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
}