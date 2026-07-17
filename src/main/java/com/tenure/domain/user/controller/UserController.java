package com.tenure.domain.user.controller;

import com.tenure.domain.user.dto.request.SignupRequest;
import com.tenure.domain.user.dto.response.SignupResponse;
import com.tenure.domain.user.service.UserService;
import com.tenure.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tenure.domain.user.dto.request.LoginRequest;
import com.tenure.domain.user.dto.response.TokenResponse;


// 사용자, 인증 관련 API
@Tag(name = "User", description = "사용자/인증 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "이메일 기반 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public BaseResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = userService.signup(request);
        return BaseResponse.success(response, "회원가입이 완료되었습니다.");
    }

    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인하고 Access Token을 발급합니다.")
    @PostMapping("/login")
    public BaseResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = userService.login(request);
        return BaseResponse.success(response, "로그인이 완료되었습니다.");
    }
}