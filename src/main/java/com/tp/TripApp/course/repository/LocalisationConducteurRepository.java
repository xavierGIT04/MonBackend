package com.tp.TripApp.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional; // ✅ Import ajouté

import com.tp.TripApp.course.entity.LocalisationConducteur;
import com.tp.TripApp.security.entity.ProfilConducteur;

public interface LocalisationConducteurRepository
        extends JpaRepository<LocalisationConducteur, Long> {

    Optional<LocalisationConducteur> findByConducteur(ProfilConducteur conducteur);

    // ─── PostGIS : conducteurs actifs dans un rayon ───────────────────────────
    @Query(value = """
        SELECT l.*
        FROM localisations_conducteurs l
        JOIN profil_conducteur c ON l.conducteur_id = c.id
        WHERE c.statut_service = 'LIBRE'
          AND c.est_valide_par_admin = true
          AND ST_DWithin(
              l.position::geography,
              ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
              :rayonMetres
          )
        ORDER BY
            l.position <-> ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
        """,
        nativeQuery = true)
    List<LocalisationConducteur> findConducteursActifsProches(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("rayonMetres") double rayonMetres
    );

    // ─── PostGIS : conducteur le plus proche pour appairage ───────────────────
    @Query(value = """
        SELECT l.*
        FROM localisations_conducteurs l
        JOIN profil_conducteur c ON l.conducteur_id = c.id
        WHERE c.statut_service = 'LIBRE'
          AND c.est_valide_par_admin = true
          AND ST_DWithin(
              l.position::geography,
              ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
              10000
          )
        ORDER BY
            l.position <-> ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
        LIMIT 1
        """,
        nativeQuery = true)
    Optional<LocalisationConducteur> findConducteurLePlusProche(
        @Param("lat") double lat,
        @Param("lng") double lng
    );

    // ─── Upsert atomique (INSERT ou UPDATE) ───────────────────────────────────
    @Modifying
    @Transactional // ✅ Ajout ici
    @Query(value = """
        INSERT INTO localisations_conducteurs (conducteur_id, position, derniere_maj)
        VALUES (
            :conducteurId,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326),
            NOW()
        )
        ON CONFLICT (conducteur_id) DO UPDATE
            SET position     = ST_SetSRID(ST_MakePoint(:lng, :lat), 4326),
                derniere_maj = NOW()
        """,
        nativeQuery = true)
    void upsertLocalisation(
        @Param("conducteurId") Long conducteurId,
        @Param("lat") double lat,
        @Param("lng") double lng
    );
}
