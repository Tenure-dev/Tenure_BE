package com.tenure.domain.auth.controller;

import com.tenure.domain.auth.dto.request.EmailSendRequest;
import com.tenure.domain.auth.service.AuthService;
import com.tenure.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "이메일 인증번호 발송", description = "입력한 이메일로 인증번호를 발송합니다. 이미 가입된 이메일이면 실패합니다.")
    @PostMapping("/email/send")
    public BaseResponse<Void> sendEmailVerification(@Valid @RequestBody EmailSendRequest request) {
        authService.sendEmailVerification(request.email());
        return BaseResponse.success(null, "인증번호가 발송되었습니다.");
    }
}