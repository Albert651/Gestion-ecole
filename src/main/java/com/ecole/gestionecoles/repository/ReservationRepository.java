package com.ecole.gestionecoles.repository;

import com.ecole.gestionecoles.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Les reservations d'un utilisateur, les plus recentes en premier
    List<Reservation> findByUtilisateurEmailOrderByDateReservationDesc(String email);

    // Toutes les reservations (pour l'admin), les plus recentes en premier
    List<Reservation> findAllByOrderByDateReservationDesc();
}