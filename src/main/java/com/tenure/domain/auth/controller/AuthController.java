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
import com.tenure.domain.auth.dto.request.EmailVerifyRequest;
import com.tenure.domain.auth.dto.response.UsernameCheckResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.tenure.domain.auth.dto.request.PasswordResetSendRequest;


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

    @Operation(summary = "이메일 인증번호 확인", description = "발송된 인증번호가 맞는지 확인합니다.")
    @PostMapping("/email/verify")
    public BaseResponse<Void> verifyEmailCode(@Valid @RequestBody EmailVerifyRequest request) {
        authService.verifyEmailCode(request.email(), request.code());
        return BaseResponse.success(null, "이메일 인증이 완료되었습니다.");
    }

    @Operation(summary = "닉네임 중복 확인", description = "닉네임이 사용 가능한지 확인합니다. available=true면 사용 가능.")
    @GetMapping("/username/check")
    public BaseResponse<UsernameCheckResponse> checkUsername(@RequestParam String username) {
        boolean available = authService.isUsernameAvailable(username);
        return BaseResponse.success(new UsernameCheckResponse(available));
    }

    @Operation(summary = "비밀번호 재설정 인증번호 발송",
            description = "가입된 이메일로 비밀번호 재설정 인증번호를 발송합니다.")
    @PostMapping("/password/reset/send")
    public BaseResponse<Void> sendPasswordResetCode(@Valid @RequestBody PasswordResetSendRequest request) {
        authService.sendPasswordResetCode(request.email());
        return BaseResponse.success(null, "인증번호가 발송되었습니다.");
    }
}