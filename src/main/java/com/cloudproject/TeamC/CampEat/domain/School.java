package com.cloudproject.TeamC.CampEat.domain;

import com.cloudproject.TeamC.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "school")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class School extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "email_domain", length = 100, nullable = false)
    private String emailDomain;

    @Column(name = "campus_location", columnDefinition = "POINT SRID 4326")
    private Point campusLocation;

    @Builder
    public School(String name, String emailDomain, Point campusLocation) {
        this.name = name;
        this.emailDomain = emailDomain;
        this.campusLocation = campusLocation;
    }
}

