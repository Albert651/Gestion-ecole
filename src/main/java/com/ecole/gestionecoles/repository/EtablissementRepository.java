package com.ecole.gestionecoles.repository;

import com.ecole.gestionecoles.model.Etablissement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * En heritant de JpaRepository, on obtient GRATUITEMENT toutes les
 * operations de base : findAll(), findById(), save(), deleteById()...
 * Pas besoin d'ecrire une seule ligne de SQL.
 */
@Repository
public interface EtablissementRepository extends JpaRepository<Etablissement, Long> {
}
