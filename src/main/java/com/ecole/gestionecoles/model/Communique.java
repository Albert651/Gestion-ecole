package com.ecole.gestionecoles.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Un communique, relie a un etablissement.
 * Detaille les modalites d'admission et les frais.
 */
@Entity
@Table(name = "communiques")
public class Communique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String modalitesAdmission;

    private String fraisInscription;
    private String fraisScolarite;

    // Periode des frais de scolarite : "Par mois", "Par semestre", "Par an"...
    private String periodeScolarite;

    // Lien vers l'etablissement concerne
    private Long etablissementId;
    private String etablissementNom;

    private LocalDateTime datePublication = LocalDateTime.now();

    public Communique() {
    }

    // ----- Getters & Setters -----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getModalitesAdmission() { return modalitesAdmission; }
    public void setModalitesAdmission(String modalitesAdmission) { this.modalitesAdmission = modalitesAdmission; }

    public String getFraisInscription() { return fraisInscription; }
    public void setFraisInscription(String fraisInscription) { this.fraisInscription = fraisInscription; }

    public String getFraisScolarite() { return fraisScolarite; }
    public void setFraisScolarite(String fraisScolarite) { this.fraisScolarite = fraisScolarite; }

    public String getPeriodeScolarite() { return periodeScolarite; }
    public void setPeriodeScolarite(String periodeScolarite) { this.periodeScolarite = periodeScolarite; }

    public Long getEtablissementId() { return etablissementId; }
    public void setEtablissementId(Long etablissementId) { this.etablissementId = etablissementId; }

    public String getEtablissementNom() { return etablissementNom; }
    public void setEtablissementNom(String etablissementNom) { this.etablissementNom = etablissementNom; }

    public LocalDateTime getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDateTime datePublication) { this.datePublication = datePublication; }
}