package com.ecole.gestionecoles.repository;

import com.ecole.gestionecoles.model.Communique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommuniqueRepository extends JpaRepository<Communique, Long> {

    List<Communique> findAllByOrderByDatePublicationDesc();
}