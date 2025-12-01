package com.cloudproject.TeamC.CampEat.infrastructure.persistence;

import com.cloudproject.TeamC.CampEat.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceJpaRepository extends JpaRepository<Place, Long> {

}

