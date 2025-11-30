package com.cloudproject.TeamC.CampEat.infrastructure.repository;

import com.cloudproject.TeamC.CampEat.domain.Place;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query(value = "SELECT p FROM Place p " +
            "WHERE ST_Distance_Sphere(p.location, :userLocation) <= :radius " +
            "AND (:categoryKeyword IS NULL OR p.categoryName LIKE %:categoryKeyword%)")
    List<Place> findPlacesNearby(
            @Param("userLocation") Point userLocation,
            @Param("radius") Double radius,
            @Param("categoryKeyword") String categoryKeyword
    );
}
