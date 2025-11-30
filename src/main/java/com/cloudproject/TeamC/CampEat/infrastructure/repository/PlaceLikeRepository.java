package com.cloudproject.TeamC.CampEat.infrastructure.repository;

import com.cloudproject.TeamC.CampEat.domain.Place;
import com.cloudproject.TeamC.CampEat.domain.PlaceLike;
import com.cloudproject.TeamC.CampEat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {
    Optional<PlaceLike> findByPlaceAndUser(Place place, User user);

    long countByPlace(Place place);
}