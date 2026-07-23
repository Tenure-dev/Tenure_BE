package com.tenure.domain.item.service;

import com.tenure.domain.item.dto.CategoryResponse;
import com.tenure.domain.item.entity.Category;
import com.tenure.domain.item.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {


    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> findAllCategory() {

        log.info("[카테고리 전체 조회] 카테고리 목록 전체 조회");
        List<Category> categories = categoryRepository.findAllByIsActiveTrueOrderByDepthAscSortOrderAsc();
        log.info("[카테고리 전체 조회] 카테고리 목록 조회 성공");
        return categories.stream().map(CategoryResponse::from).toList();
    }
}
