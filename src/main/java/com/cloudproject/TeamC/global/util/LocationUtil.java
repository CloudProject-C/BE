package com.cloudproject.TeamC.global.util;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
public class LocationUtil {

    private static final int EARTH_RADIUS_KM = 6371;

    public static double calculateDistance(Point p1, Point p2) {
        if (p1 == null || p2 == null) return Double.MAX_VALUE;
        return calculateDistance(p1.getY(), p1.getX(), p2.getY(), p2.getX());
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}