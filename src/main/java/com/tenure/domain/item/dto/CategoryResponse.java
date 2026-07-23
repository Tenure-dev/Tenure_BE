package com.tenure.domain.item.dto;

import com.tenure.domain.item.entity.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryResponse {

    private Long id;
    private String name;
    private Long parentId; //parent가 null이면 null

    public static CategoryResponse from(Category category) {

        Long parentId = category.getParent() == null ? null : category.getParent().getId();

        return new CategoryResponse(category.getId(), category.getName(), parentId);
    }

}
