package com.ecole.gestionecoles.repository;

import com.ecole.gestionecoles.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Spring genere ces requetes a partir du nom des methodes
    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);
}