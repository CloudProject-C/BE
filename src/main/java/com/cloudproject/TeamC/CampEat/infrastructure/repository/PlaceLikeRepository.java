package com.cloudproject.TeamC.CampEat.infrastructure.repository;

import com.cloudproject.TeamC.CampEat.domain.Place;
import com.cloudproject.TeamC.CampEat.domain.PlaceLike;
import com.cloudproject.TeamC.CampEat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {
    Optional<PlaceLike> findByPlaceAndUser(Place place, User user);

    long countByPlace(Place place);

    @Query("SELECT pl.place.id FROM PlaceLike pl WHERE pl.user.id = :userId AND pl.place.id IN :placeIds")
    List<Long> findLikedPlaceIds(@Param("userId") Long userId, @Param("placeIds") List<Long> placeIds);

    boolean existsByPlaceAndUser(Place place, User user);
}