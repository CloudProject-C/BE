package com.cloudproject.TeamC.CampEat.infrastructure.repository;

import com.cloudproject.TeamC.CampEat.domain.Place;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlaceRepository{
    private final JdbcTemplate jdbcTemplate;

    private final PlaceJpaRepository jpaRepository;

    public List<Place> findAll() {
        return jpaRepository.findAll();
    }
    public Optional<Place> findById(Long id) {
        return jpaRepository.findById(id);
    }
//    public Place findById(Long id) {
//        return jpaRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Place not found"));
//    }

    public Long countBySchoolId(Long schoolId) {
        return jpaRepository.countBySchoolId(schoolId);
    }

    public List<Place> findPlacesNearby(Point userLocation, Double radius, String categoryKeyword) {
        return jpaRepository.findPlacesNearby(userLocation, radius, categoryKeyword);
    }


    public Place findByIdWithJpa(Long id) {
        return jpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place not found"));
    }

    public void saveAll(List<Place> places) {
        String sql =
                "INSERT INTO place (" +
                        "id, school_id, place_name, category_group_code, category_group_name, category_name, " +
                        "phone, address_name, road_address_name, location, distance, place_url" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ST_GeomFromText(?, 4326), ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "place_name = VALUES(place_name), " +
                        "category_group_code = VALUES(category_group_code), " +
                        "category_group_name = VALUES(category_group_name), " +
                        "category_name = VALUES(category_name), " +
                        "phone = VALUES(phone), " +
                        "address_name = VALUES(address_name), " +
                        "road_address_name = VALUES(road_address_name), " +
                        "location = VALUES(location), " +
                        "distance = VALUES(distance), " +
                        "place_url = VALUES(place_url)";

        jdbcTemplate.batchUpdate(
                sql,
                places,
                places.size(),
                (PreparedStatement ps, Place p) -> {
                    ps.setLong(1, p.getId());
                    ps.setLong(2, p.getSchool().getId());
                    ps.setString(3, p.getPlaceName());
                    ps.setString(4, p.getCategoryGroupCode());
                    ps.setString(5, p.getCategoryGroupName());
                    ps.setString(6, p.getCategoryName());
                    ps.setString(7, p.getPhone());
                    ps.setString(8, p.getAddressName());
                    ps.setString(9, p.getRoadAddressName());

                    if (p.getLocation() != null) {
                        double lon = p.getLocation().getX(); // 경도
                        double lat = p.getLocation().getY(); // 위도

                        // MySQL ST_GeomFromText with SRID 4326: POINT(latitude longitude) 순서!
                        String wkt = String.format(Locale.US, "POINT(%.8f %.8f)", lat, lon);
                        ps.setString(10, wkt);
                    } else {
                        ps.setNull(10, Types.VARCHAR);
                    }

                    if (p.getDistance() == null) {
                        ps.setNull(11, Types.INTEGER);
                    } else {
                        ps.setInt(11, p.getDistance());
                    }

                    ps.setString(12, p.getPlaceUrl());
                }
        );
    }


//    public void saveAll(List<Place> places) {
//        String sql = "INSERT INTO place (" +
//                "id, school_id, place_name, category_group_code, category_group_name, category_name, " +
//                "phone, address_name, road_address_name, location, distance, place_url" +
//                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ST_PointFromText(?, 4326), ?, ?) " +
//                "ON DUPLICATE KEY UPDATE " +
//                "place_name = VALUES(place_name), " +
//                "category_group_code = VALUES(category_group_code), " +
//                "category_group_name = VALUES(category_group_name), " +
//                "category_name = VALUES(category_name), " +
//                "phone = VALUES(phone), " +
//                "address_name = VALUES(address_name), " +
//                "road_address_name = VALUES(road_address_name), " +
//                "location = VALUES(location), " +
//                "distance = VALUES(distance), " +
//                "place_url = VALUES(place_url)";
//
//        jdbcTemplate.batchUpdate(
//                sql,
//                places,
//                places.size(),
//                (PreparedStatement ps, Place p) -> {
//                    ps.setLong(1, p.getId());
//                    ps.setLong(2, p.getSchool().getId());
//                    ps.setString(3, p.getPlaceName());
//                    ps.setString(4, p.getCategoryGroupCode());
//                    ps.setString(5, p.getCategoryGroupName());
//                    ps.setString(6, p.getCategoryName());
//                    ps.setString(7, p.getPhone());
//                    ps.setString(8, p.getAddressName());
//                    ps.setString(9, p.getRoadAddressName());
//
//                    // POINT → "POINT(lon lat)" 문자열 생성
//                    String pointWKT = String.format(
//                            "POINT(%f %f)",
//                            p.getLocation().getX(),  // longitude
//                            p.getLocation().getY()   // latitude
//                    );
//                    ps.setString(10, pointWKT);
//
//                    if (p.getDistance() == null) {
//                        ps.setNull(11, Types.INTEGER);
//                    } else {
//                        ps.setInt(11, p.getDistance());
//                    }
//
//                    ps.setString(12, p.getPlaceUrl());
//                }
//        );
//    }



}
