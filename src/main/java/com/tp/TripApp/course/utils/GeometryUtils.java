package com.tp.TripApp.course.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

/**
 * Utilitaire pour créer des objets géométriques PostGIS.
 *
 * SRID 4326 = WGS84 (système GPS standard — latitude/longitude)
 * Convention JTS : Point(x=longitude, y=latitude)
 */
public class GeometryUtils {

    // SRID 4326 = WGS84 (GPS)
    public static final int SRID = 4326;

    private static final GeometryFactory FACTORY =
        new GeometryFactory(new PrecisionModel(), SRID);

    /**
     * Crée un Point PostGIS depuis lat/lng.
     * @param latitude  Y (nord/sud)
     * @param longitude X (est/ouest)
     */
    public static Point createPoint(double latitude, double longitude) {
        Point point = FACTORY.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(SRID);
        return point;
    }

    /**
     * Calcule la distance en km entre deux points (formule Haversine côté Java)
     * Utilisé pour les estimations avant persistance.
     * Pour les requêtes DB, on laisse PostGIS faire le calcul.
     */
    public static double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    /** Prix estimé : 300 FCFA base + 150 FCFA/km */
    public static double calculerPrix(double distanceKm) {
        return Math.round(300 + (distanceKm * 150));
    }
}
