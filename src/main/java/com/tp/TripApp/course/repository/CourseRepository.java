package com.tp.TripApp.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tp.TripApp.course.entity.Course;
import com.tp.TripApp.course.enums.StatutCourse;
import com.tp.TripApp.security.entity.CompteUtilisateur;
import com.tp.TripApp.security.entity.ProfilConducteur;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // ─── Courses actives ──────────────────────────────────────────────────────

    Optional<Course> findFirstByPassagerAndStatutIn(
        CompteUtilisateur passager, List<StatutCourse> statuts);

    Optional<Course> findFirstByConducteurAndStatutIn(
        ProfilConducteur conducteur, List<StatutCourse> statuts);

    // ─── Historiques ──────────────────────────────────────────────────────────

    List<Course> findByPassagerOrderByDate_CommandeDesc(CompteUtilisateur passager);

    List<Course> findByConducteurOrderByDate_CommandeDesc(ProfilConducteur conducteur);

    // ─── PostGIS : courses EN_ATTENTE proches d'un conducteur ────────────────
    /**
     * Cherche les courses EN_ATTENTE dont le point de départ est
     * dans un rayon de :rayonMetres mètres autour du conducteur.
     *
     * ST_DWithin(geography) = rayon exact sur la sphère (mètres)
     * <-> = opérateur KNN pour le tri par proximité
     *
     * @param lng         longitude du conducteur
     * @param lat         latitude du conducteur
     * @param rayonMetres rayon de recherche (ex: 5000 = 5km)
     */
    @Query(value = """
        SELECT c.*
        FROM courses c
        WHERE c.statut = 'EN_ATTENTE'
          AND ST_DWithin(
              c.depart_position::geography,
              ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
              :rayonMetres
          )
        ORDER BY
            c.depart_position <-> ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
        """,
        nativeQuery = true)
    List<Course> findCoursesEnAttenteProches(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("rayonMetres") double rayonMetres
    );

    // ─── PostGIS : distance entre deux points de la course ───────────────────
    /**
     * Calcule la distance réelle en km entre départ et destination
     * directement via PostGIS (ST_Distance sur geography = mètres).
     * Résultat divisé par 1000 pour avoir des km.
     */
    @Query(value = """
        SELECT
            ST_Distance(
                depart_position::geography,
                destination_position::geography
            ) / 1000.0
        FROM courses
        WHERE id = :courseId
        """,
        nativeQuery = true)
    Double calculerDistanceKm(@Param("courseId") Long courseId);

    // ─── Statistiques conducteur ──────────────────────────────────────────────

    @Query("""
        SELECT AVG(c.note_conducteur)
        FROM Course c
        WHERE c.conducteur = :conducteur
          AND c.note_conducteur IS NOT NULL
    """)
    Double calculerNoteMoyenne(@Param("conducteur") ProfilConducteur conducteur);

    @Query("""
        SELECT COUNT(c)
        FROM Course c
        WHERE c.conducteur = :conducteur
          AND c.statut = 'TERMINEE'
    """)
    Long countCoursesTerminees(@Param("conducteur") ProfilConducteur conducteur);

    @Query("""
    	    SELECT COALESCE(SUM(c.prixFinal), 0.0)
    	    FROM Course c
    	    WHERE c.conducteur = :conducteur
    	      AND c.statut = 'TERMINEE'
    	      AND c.dateFin >= CURRENT_DATE
    	      AND c.dateFin < (CURRENT_DATE + 1)
    	""")
    Double gainsDuJour(@Param("conducteur") ProfilConducteur conducteur);
}
