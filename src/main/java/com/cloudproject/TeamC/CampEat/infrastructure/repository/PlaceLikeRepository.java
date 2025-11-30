package com.cloudproject.TeamC.CampEat.infrastructure.repository;

import com.cloudproject.TeamC.CampEat.domain.Place;
import com.cloudproject.TeamC.CampEat.domain.PlaceLike;
import com.cloudproject.TeamC.CampEat.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {
    Optional<PlaceLike> findByPlaceAndUser(Place place, User user);

    boolean existsByPlace_IdAndUser_Id(Long placeId, Long userId);

    long countByPlace(Place place);

    // [신규] 배치: 좋아요 개수 (placeId별 그룹핑)
    @Query("SELECT pl.place.id, COUNT(pl) " +
            "FROM PlaceLike pl " +
            "WHERE pl.place.id IN :placeIds " +
            "GROUP BY pl.place.id")
    List<Object[]> countLikesByPlaceIds(@Param("placeIds") List<Long> placeIds);

    // [신규] 배치: 내가 좋아요한 장소 ID 목록
    @Query("SELECT pl.place.id " +
            "FROM PlaceLike pl " +
            "WHERE pl.user.id = :userId AND pl.place.id IN :placeIds")
    List<Long> findLikedPlaceIds(@Param("userId") Long userId, @Param("placeIds") List<Long> placeIds);

    Long countByUserId(Long userId);

    Page<PlaceLike> findByUserId(Long userId, Pageable pageable);
}