package com.cloudproject.TeamC.CampEat.domain;

import com.cloudproject.TeamC.global.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Place extends BaseEntity {

    @Id
    private Long id;
    private String placeName;
    private String categoryGroupCode;
    private String categoryGroupName;
    private String categoryName;
    private String phone;
    private String addressName;
    private String roadAddressName;
    private Double x;
    private Double y;
    private Integer distance;
    private String placeUrl;

    @Builder
    public Place(Long id, String placeName, String categoryGroupCode, String categoryGroupName,
                 String categoryName, String phone, String addressName, String roadAddressName,
                 Double x, Double y, Integer distance, String placeUrl) {
        this.id = id;
        this.placeName = placeName;
        this.categoryGroupCode = categoryGroupCode;
        this.categoryGroupName = categoryGroupName;
        this.categoryName = categoryName;
        this.phone = phone;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.x = x;
        this.y = y;
        this.distance = distance;
        this.placeUrl = placeUrl;
    }

}
