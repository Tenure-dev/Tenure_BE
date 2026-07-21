package com.tenure.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// 인증 메일 발송
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // 인증번호 메일 발송
    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[Tenure] 이메일 인증번호 안내");
        message.setText("인증번호는 [" + code + "] 입니다.\n5분 이내에 입력해주세요.");

        mailSender.send(message);
        log.info("인증번호 메일 발송 완료: {}", toEmail);
    }
}