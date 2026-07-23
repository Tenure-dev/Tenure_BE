package com.tenure.domain.item.controller;

import com.tenure.domain.item.dto.CategoryResponse;
import com.tenure.domain.item.service.CategoryService;
import com.tenure.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Category", description = "카테고리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 전체 조회", description = "활성화된 카테고리 목록을 전체 반환합니다.")
    @GetMapping
    public BaseResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> allCategory = categoryService.findAllCategory();
        return BaseResponse.success(allCategory);
    }
}
