package com.tenure.domain.user.repository;

import com.tenure.domain.search.dto.response.SearchUserQueryDto;
import com.tenure.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    //키워드로 시작하는 유저조회(id, name, imgurl, followerCount, ootdCount)
    @Query("select new com.tenure.domain.search.dto.response.SearchUserQueryDto(" +
            "u.id, u.username, u.profileImageUrl, " +
            "(select count(uf) from FollowRelationship uf " +
            "where uf.following.id = u.id and uf.status = com.tenure.domain.follow.enums.FollowStatus.ACCEPTED ), " +
            "(select count(o) from Ootd o " +
            "where o.owner.id = u.id and o.publicationStatus = com.tenure.domain.ootd.enums.OotdPublicationStatus.ACTIVE )) " +
            "from User u " +
            "where lower( u.username) like lower(concat(:keyword, '%')) " +
            "and u.id < :cursorId " +
            "order by u.id desc ")
    Slice<SearchUserQueryDto> searchUsers(
            @Param("keyword") String keyword,
            @Param("cursorId") Long cursorId,
            Pageable request);

    // 검색 홈 — 인기 사용자 (팔로워 수 내림차순, 커서 방식)
    @Query("select new com.tenure.domain.search.dto.response.SearchUserQueryDto(" +
            "u.id, u.username, u.profileImageUrl, " +
            "(select count(uf) from FollowRelationship uf " +
            "where uf.following.id = u.id and uf.status = com.tenure.domain.follow.enums.FollowStatus.ACCEPTED), " +
            "(select count(o) from Ootd o " +
            "where o.owner.id = u.id and o.publicationStatus = com.tenure.domain.ootd.enums.OotdPublicationStatus.ACTIVE)) " +
            "from User u " +
            "where not exists (select 1 from UserBlock block " +
            "   where (block.blocker.id = :currentUserId and block.blocked.id = u.id) " +
            "      or (block.blocker.id = u.id and block.blocked.id = :currentUserId)) " +
            "and ((select count(uf2) from FollowRelationship uf2 " +
            "       where uf2.following.id = u.id and uf2.status = com.tenure.domain.follow.enums.FollowStatus.ACCEPTED) < :cursorFollowerCount " +
            "   or ((select count(uf3) from FollowRelationship uf3 " +
            "        where uf3.following.id = u.id and uf3.status = com.tenure.domain.follow.enums.FollowStatus.ACCEPTED) = :cursorFollowerCount " +
            "       and u.id < :cursorId)) " +
            "order by (select count(uf4) from FollowRelationship uf4 " +
            "          where uf4.following.id = u.id and uf4.status = com.tenure.domain.follow.enums.FollowStatus.ACCEPTED) desc, u.id desc")
    Slice<SearchUserQueryDto> findPopularUsers(
            @Param("cursorFollowerCount") Long cursorFollowerCount,
            @Param("cursorId") Long cursorId,
            @Param("currentUserId") Long currentUserId,
            Pageable pageable);

    // email 중복 검사용. 가입 시 이미 존재하는 이메일이면 true 반환
    // Spring Data JPA가 존재여부 쿼리를 자동 생성
    boolean existsByEmail(String email);

    // username 중복 검사용. 가입 시 중복 닉네임이면 true 반환
    boolean existsByUsername(String username);

    // 로그인 시 이메일로 user 찾기
    Optional<User> findByEmail(String email);
}
