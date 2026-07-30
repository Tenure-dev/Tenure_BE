package com.tenure.domain.auth.service;

import com.tenure.domain.auth.exception.AuthErrorCode;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.tenure.domain.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;


// 인증 로직
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final EmailVerificationStore verificationStore;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.email-verification-enabled:true}")
    private boolean emailVerificationEnabled;

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

    /**
     * 비밀번호 재설정용 인증번호 발송.
     * 회원가입 발송과 반대로, "가입된 이메일이어야만" 발송.
     * 인증번호 저장소는 회원가입과 동일한 것을 사용 (이메일 -> 코드).
     */
    public void sendPasswordResetCode(String email) {
        // 가입된 이메일인지 확인 (없는 계정의 비밀번호는 재설정할 수 없음)
        if (!userRepository.existsByEmail(email)) {
            throw new CustomException(AuthErrorCode.EMAIL_NOT_FOUND);
        }

        // 6자리 랜덤 인증번호 생성
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);

        verificationStore.save(email, code);
        emailService.sendPasswordResetCode(email, code);
    }

    /**
     * 새 비밀번호 설정 (비밀번호 재설정 마지막 단계).
     * 이메일 인증 완료 여부를 확인한 뒤 새 비밀번호를 저장한다.
     * (app.email-verification-enabled=false 이면 인증 확인을 건너뜀)
     */
    @Transactional
    public void resetPassword(String email, String newPassword, String newPasswordConfirm) {

        // 1) 새 비밀번호 일치 확인
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new CustomException(AuthErrorCode.PASSWORD_MISMATCH);
        }

        // 2) 이메일 인증 완료 여부 확인 (플래그 꺼져 있으면 건너뜀)
        if (emailVerificationEnabled && !verificationStore.isVerified(email)) {
            throw new CustomException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 3) 가입된 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.EMAIL_NOT_FOUND));

        // 4) 새 비밀번호 암호화 후 저장 (변경 감지로 자동 UPDATE)
        user.changePassword(passwordEncoder.encode(newPassword));

        // 5) 사용한 인증 정보 정리 (이메일 인증을 사용한 경우에만)
        if (emailVerificationEnabled) {
            verificationStore.remove(email);
        }

        log.info("비밀번호 재설정 완료: email={}", email);
    }
}