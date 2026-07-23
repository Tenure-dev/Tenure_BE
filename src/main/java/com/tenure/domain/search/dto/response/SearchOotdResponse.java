package com.tenure.domain.search.dto.response;

import com.tenure.domain.ootd.entity.Ootd;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchOotdResponse {

    private Long id;
    private String imageUrl;
    private boolean hearted; // 좋아요 유무
    private boolean saved; // 저장 유무



    public static SearchOotdResponse from(Ootd ootd, Set<Long> heartedOotdIds, Set<Long> saveOotdIds) {
       return new SearchOotdResponse(ootd.getId(), ootd.getImageUrl(),
               heartedOotdIds.contains(ootd.getId()), saveOotdIds.contains(ootd.getId()));
    }

}
