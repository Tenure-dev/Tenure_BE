package com.tenure.domain.search.dto.response;

import com.tenure.domain.ootd.entity.Ootd;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchOotdResponse {

    private Long id;
    private String imageUrl;


    public static SearchOotdResponse from(Ootd ootd) {
       return new SearchOotdResponse(ootd.getId(), ootd.getImageUrl());
    }
}
