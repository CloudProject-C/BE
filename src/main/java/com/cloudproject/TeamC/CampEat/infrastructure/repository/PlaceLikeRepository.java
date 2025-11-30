package com.cloudproject.TeamC.CampEat.infrastructure.repository;

import com.cloudproject.TeamC.CampEat.domain.Place;
import com.cloudproject.TeamC.CampEat.domain.PlaceLike;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {
    long countByPlace(Place place);
}