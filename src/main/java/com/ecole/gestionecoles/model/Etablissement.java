package com.ecole.gestionecoles.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Une entite = une table dans la base de donnees.
 * Spring va creer automatiquement la table "etablissements"
 * a partir de cette classe (grace a ddl-auto=update).
 */
@Entity
@Table(name = "etablissements")
public class Etablissement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    // "columnDefinition = TEXT" autorise les longs textes
    @Column(columnDefinition = "TEXT")
    private String description;

    private String adresse;

    // Les caracteristiques uniques affichees sur la page de detail
    @Column(columnDefinition = "TEXT")
    private String caracteristiques;

    // Lien vers l'image stockee dans Supabase Storage
    private String imageUrl;

    // Coordonnees du directeur (page contact)
    private String email;

    private String telephone;

    // ----- Constructeurs -----

    public Etablissement() {
    }

    // ----- Getters & Setters -----
    // (Spring/JPA en a besoin pour lire et ecrire les champs)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCaracteristiques() {
        return caracteristiques;
    }

    public void setCaracteristiques(String caracteristiques) {
        this.caracteristiques = caracteristiques;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
