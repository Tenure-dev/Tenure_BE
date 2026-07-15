package com.tenure.domain.user.repository;

import com.tenure.domain.search.dto.response.SearchUserQueryDto;
import com.tenure.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


}
