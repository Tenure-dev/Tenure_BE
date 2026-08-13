package com.tenure.domain.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 이메일 발송 담당 (Resend HTTP API).
 * 배포 환경(Render)에서 SMTP 포트가 차단되어 HTTPS 기반 Resend API로 발송한다.
 */
@Slf4j
@Service
public class EmailService {

    private static final String RESEND_ENDPOINT = "https://api.resend.com/emails";

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.from}")
    private String fromAddress;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 회원가입 인증번호 메일 발송 */
    public void sendVerificationCode(String toEmail, String code) {
        String subject = "[Tenure] 이메일 인증번호 안내";
        String text = "인증번호는 [" + code + "] 입니다.\n5분 이내에 입력해주세요.";
        send(toEmail, subject, text);
        log.info("인증번호 메일 발송 완료: {}", toEmail);
    }

    /** 비밀번호 재설정 인증번호 메일 발송 */
    public void sendPasswordResetCode(String toEmail, String code) {
        String subject = "[Tenure] 비밀번호 재설정 인증번호 안내";
        String text = "비밀번호 재설정 인증번호는 [" + code + "] 입니다.\n"
                + "5분 이내에 입력해주세요.\n"
                + "본인이 요청하지 않았다면 이 메일을 무시해주세요.";
        send(toEmail, subject, text);
        log.info("비밀번호 재설정 메일 발송 완료: {}", toEmail);
    }

    /** Resend API로 실제 메일 발송 */
    private void send(String toEmail, String subject, String text) {
        try {
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "from", fromAddress,
                    "to", toEmail,
                    "subject", subject,
                    "text", text
            ));

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(RESEND_ENDPOINT))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("Resend 메일 발송 실패: status={}, body={}",
                        response.statusCode(), response.body());
                throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
            }

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Resend 메일 발송 중 예외 발생: {}", e.getMessage(), e);
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}