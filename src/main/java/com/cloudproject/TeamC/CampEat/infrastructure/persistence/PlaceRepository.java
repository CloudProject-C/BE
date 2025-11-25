package com.cloudproject.TeamC.CampEat.infrastructure.persistence;


import com.cloudproject.TeamC.CampEat.domain.Place;
import com.cloudproject.TeamC.CampEat.infrastructure.persistence.PlaceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;

@Repository
@RequiredArgsConstructor

public class PlaceRepository {
    private final JdbcTemplate jdbcTemplate;

    private final PlaceJpaRepository jpaRepository;

    public List<Place> findAll() {
        return jpaRepository.findAll();
    }

    public Place findByIdWithJpa(Long id) {
        return jpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place not found"));
    }


    public void saveAll(List<Place> places) {
        String sql = "INSERT INTO place (" +
                "id, place_name, category_group_code, category_group_name, category_name, " +
                "phone, address_name, road_address_name, x, y, distance, place_url" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "place_name = VALUES(place_name), " +
                "category_group_code = VALUES(category_group_code), " +
                "category_group_name = VALUES(category_group_name), " +
                "category_name = VALUES(category_name), " +
                "phone = VALUES(phone), " +
                "address_name = VALUES(address_name), " +
                "road_address_name = VALUES(road_address_name), " +
                "x = VALUES(x), " +
                "y = VALUES(y), " +
                "distance = VALUES(distance), " +
                "place_url = VALUES(place_url)";

        jdbcTemplate.batchUpdate(
                sql,
                places,
                places.size(),
                (PreparedStatement ps, Place p) -> {
                    ps.setLong(1, p.getId());
                    ps.setString(2, p.getPlaceName());
                    ps.setString(3, p.getCategoryGroupCode());
                    ps.setString(4, p.getCategoryGroupName());
                    ps.setString(5, p.getCategoryName());
                    ps.setString(6, p.getPhone());
                    ps.setString(7, p.getAddressName());
                    ps.setString(8, p.getRoadAddressName());
                    ps.setDouble(9, p.getX());
                    ps.setDouble(10, p.getY());
                    if (p.getDistance() == null) {
                        ps.setNull(11, Types.INTEGER);
                    } else {
                        ps.setInt(11, p.getDistance());
                    }
                    ps.setString(12, p.getPlaceUrl());
                }
        );
    }
}
