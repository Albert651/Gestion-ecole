package com.ecole.gestionecoles.controller;

import com.ecole.gestionecoles.model.Etablissement;
import com.ecole.gestionecoles.repository.EtablissementRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Le controller expose les routes HTTP de l'API.
 * Base : http://localhost:8080/api/etablissements
 */
@RestController
@RequestMapping("/api/etablissements")
public class EtablissementController {

    private final EtablissementRepository repository;

    // Spring "injecte" automatiquement le repository ici
    public EtablissementController(EtablissementRepository repository) {
        this.repository = repository;
    }

    // GET /api/etablissements  -> liste de tous les etablissements
    @GetMapping
    public List<Etablissement> listerTous() {
        return repository.findAll();
    }

    // GET /api/etablissements/1  -> detail d'un etablissement
    @GetMapping("/{id}")
    public ResponseEntity<Etablissement> obtenirParId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/etablissements  -> creer un etablissement
    @PostMapping
    public Etablissement creer(@RequestBody Etablissement etablissement) {
        return repository.save(etablissement);
    }

    // PUT /api/etablissements/1  -> modifier un etablissement
    @PutMapping("/{id}")
    public ResponseEntity<Etablissement> modifier(@PathVariable Long id,
                                                  @RequestBody Etablissement details) {
        return repository.findById(id).map(etab -> {
            etab.setNom(details.getNom());
            etab.setDescription(details.getDescription());
            etab.setAdresse(details.getAdresse());
            etab.setCaracteristiques(details.getCaracteristiques());
            etab.setImageUrl(details.getImageUrl());
            etab.setEmail(details.getEmail());
            etab.setTelephone(details.getTelephone());
            return ResponseEntity.ok(repository.save(etab));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/etablissements/1  -> supprimer un etablissement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
