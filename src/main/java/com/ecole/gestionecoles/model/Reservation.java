package com.ecole.gestionecoles.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Une reservation d'un etablissement par un utilisateur connecte.
 * statut : "EN_ATTENTE", "CONFIRMEE" ou "ANNULEE".
 */
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // L'email de l'utilisateur qui reserve (rempli cote serveur)
    private String utilisateurEmail;

    private Long etablissementId;
    private String etablissementNom;

    // --- Champs du formulaire de reservation ---
    private String nomComplet;
    private String telephone;
    private LocalDate dateSouhaitee;

    @Column(columnDefinition = "TEXT")
    private String note;

    private String statut = "EN_ATTENTE";

    private LocalDateTime dateReservation = LocalDateTime.now();

    public Reservation() {
    }

    // ----- Getters & Setters -----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUtilisateurEmail() { return utilisateurEmail; }
    public void setUtilisateurEmail(String utilisateurEmail) { this.utilisateurEmail = utilisateurEmail; }

    public Long getEtablissementId() { return etablissementId; }
    public void setEtablissementId(Long etablissementId) { this.etablissementId = etablissementId; }

    public String getEtablissementNom() { return etablissementNom; }
    public void setEtablissementNom(String etablissementNom) { this.etablissementNom = etablissementNom; }

    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public LocalDate getDateSouhaitee() { return dateSouhaitee; }
    public void setDateSouhaitee(LocalDate dateSouhaitee) { this.dateSouhaitee = dateSouhaitee; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }
}