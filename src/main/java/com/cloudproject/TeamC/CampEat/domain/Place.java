package com.cloudproject.TeamC.CampEat.domain;

import com.cloudproject.TeamC.global.domain.BaseEntity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Getter
@Entity
@Table(name = "place")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Place extends BaseEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "place_name", length = 255)
    private String placeName;

    @Column(name = "category_group_code", length = 255)
    private String categoryGroupCode;

    @Column(name = "category_group_name", length = 255)
    private String categoryGroupName;

    @Column(name = "category_name", length = 255)
    private String categoryName;

    @Column(name = "phone", length = 255)
    private String phone;

    @Column(name = "address_name", length = 255)
    private String addressName;

    @Column(name = "road_address_name", length = 255)
    private String roadAddressName;

    /**
     * POINT(longitude, latitude)
     * MySQL Geometry type
     */
    @Column(name = "location", columnDefinition = "POINT SRID 4326")
    private Point location;

    @Column(name = "distance")
    private Integer distance;

    @Column(name = "place_url", length = 255)
    private String placeUrl;

    @Builder
    private Place(
            Long id,
            School school,
            String placeName,
            String categoryGroupCode,
            String categoryGroupName,
            String categoryName,
            String phone,
            String addressName,
            String roadAddressName,
            Point location,
            Integer distance,
            String placeUrl
    ) {
        this.id = id;
        this.school = school;
        this.placeName = placeName;
        this.categoryGroupCode = categoryGroupCode;
        this.categoryGroupName = categoryGroupName;
        this.categoryName = categoryName;
        this.phone = phone;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.location = location;
        this.distance = distance;
        this.placeUrl = placeUrl;
    }
}
