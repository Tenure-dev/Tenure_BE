package com.tenure.global;

import com.tenure.global.response.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 동작 검증용 임시 컨트롤러 — JWT 인증 도입 후 삭제 예정.
 */
@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public BaseResponse<String> health() {
        return BaseResponse.success("ok");
    }
}
