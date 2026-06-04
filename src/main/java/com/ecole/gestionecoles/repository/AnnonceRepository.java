package com.ecole.gestionecoles.repository;

import com.ecole.gestionecoles.model.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long> {

    // Spring genere automatiquement cette requete a partir du nom de la methode :
    // "trouve toutes les annonces, triees par date de publication decroissante"
    List<Annonce> findAllByOrderByDatePublicationDesc();
}
