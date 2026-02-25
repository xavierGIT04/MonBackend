package com.tp.TripApp.course.repository;

import java.time.LocalDateTime;
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

    @Query("SELECT c FROM Course c WHERE c.passager = :passager ORDER BY c.date_commande DESC")
    List<Course> findByPassagerOrderByDateCommandeDesc(@Param("passager") CompteUtilisateur passager);

    @Query("SELECT c FROM Course c WHERE c.conducteur = :conducteur ORDER BY c.date_commande DESC")
    List<Course> findByConducteurOrderByDateCommandeDesc(@Param("conducteur") ProfilConducteur conducteur);

    // ─── PostGIS : courses EN_ATTENTE proches d'un conducteur ────────────────
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

    // Hibernate 6 n'accepte pas CURRENT_DATE + 1 → on passe les bornes en paramètres
    // Appeler avec : debutJour = LocalDateTime.now().toLocalDate().atStartOfDay()
    //                finJour   = debutJour.plusDays(1)
    @Query("""
        SELECT COALESCE(SUM(c.prix_final), 0.0)
        FROM Course c
        WHERE c.conducteur = :conducteur
          AND c.statut = com.tp.TripApp.course.enums.StatutCourse.TERMINEE
          AND c.date_fin >= :debutJour
          AND c.date_fin < :finJour
    """)
    Double gainsDuJour(
        @Param("conducteur") ProfilConducteur conducteur,
        @Param("debutJour")  LocalDateTime debutJour,
        @Param("finJour")    LocalDateTime finJour
    );
    
    // ─── 1. Compter les courses par statut unique ─────────────────────────
    @Query("SELECT COUNT(c) FROM Course c WHERE c.statut = :statut")
    long countByStatut(@Param("statut") StatutCourse statut);

// ─── 2. Compter les courses parmi une liste de statuts ───────────────
    @Query("SELECT COUNT(c) FROM Course c WHERE c.statut IN :statuts")
    long countByStatutIn(@Param("statuts") List<StatutCourse> statuts);

// ─── 3. Courses terminées dans un intervalle (pour stats du jour) ────
// Utilise date_fin (champ Java snake_case) — @Query obligatoire
    @Query("""
        SELECT COUNT(c) FROM Course c
        WHERE c.statut = :statut
          AND c.date_fin >= :debut
          AND c.date_fin < :fin
    """)
    long countTermineesEntre(
        @Param("statut") StatutCourse statut,
        @Param("debut") LocalDateTime debut,
        @Param("fin") LocalDateTime fin
    );

// ─── 4. Liste des courses actives (trafic live) ──────────────────────
    @Query("SELECT c FROM Course c WHERE c.statut IN :statuts ORDER BY c.date_commande DESC")
    List<Course> findByStatutIn(@Param("statuts") List<StatutCourse> statuts);

// ─── 5. Somme des prix finaux du jour ────────────────────────────────
// Utilise prix_final (snake_case) — @Query obligatoire
    @Query("""
        SELECT COALESCE(SUM(c.prix_final), 0.0) FROM Course c
        WHERE c.statut = :statut
          AND c.date_fin >= :debut
          AND c.date_fin < :fin
    """)
    Double sumPrixFinalEntre(
        @Param("statut") StatutCourse statut,
        @Param("debut") LocalDateTime debut,
        @Param("fin") LocalDateTime fin
    );

}