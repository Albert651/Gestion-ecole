package com.ecole.gestionecoles.repository;

import com.ecole.gestionecoles.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Messages tries du plus recent au plus ancien
    List<Message> findAllByOrderByDateEnvoiDesc();
}
