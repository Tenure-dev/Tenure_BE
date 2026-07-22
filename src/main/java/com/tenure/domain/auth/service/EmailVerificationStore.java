package com.tenure.domain.auth.service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

// 이메일 인증 번호 저장
@Component
public class EmailVerificationStore {

    private static final long EXPIRE_MINUTES = 5;

    private final ConcurrentHashMap<String, Verification> store = new ConcurrentHashMap<>();

    // 인증번호 저장 (발송 시)
    public void save(String email, String code) {
        store.put(email, new Verification(code, LocalDateTime.now().plusMinutes(EXPIRE_MINUTES), false));
    }

    // 인증번호 확인. 일치+미만료면 true
    public boolean verify(String email, String code) {
        Verification v = store.get(email);
        if (v == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(v.expiresAt())) {
            store.remove(email);
            return false;
        }
        if (!v.code().equals(code)) {
            return false;
        }
        store.put(email, new Verification(v.code(), v.expiresAt(), true));
        return true;
    }

    // 인증 완료 여부 (최종 회원가입 검증용)
    public boolean isVerified(String email) {
        Verification v = store.get(email);
        return v != null && v.verified() && LocalDateTime.now().isBefore(v.expiresAt());
    }

    // 저장소에서 삭제
    public void remove(String email) {
        store.remove(email);
    }

    private record Verification(String code, LocalDateTime expiresAt, boolean verified) {
    }
}