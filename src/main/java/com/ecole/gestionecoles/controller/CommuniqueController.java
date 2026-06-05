package com.ecole.gestionecoles.controller;

import com.ecole.gestionecoles.model.Communique;
import com.ecole.gestionecoles.repository.CommuniqueRepository;
import com.ecole.gestionecoles.repository.EtablissementRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Routes : http://localhost:8080/api/communiques
 */
@RestController
@RequestMapping("/api/communiques")
public class CommuniqueController {

    private final CommuniqueRepository repository;
    private final EtablissementRepository etablissementRepository;

    public CommuniqueController(CommuniqueRepository repository,
                                EtablissementRepository etablissementRepository) {
        this.repository = repository;
        this.etablissementRepository = etablissementRepository;
    }

    @GetMapping
    public List<Communique> listerTous() {
        return repository.findAllByOrderByDatePublicationDesc();
    }

    @PostMapping
    public Communique creer(@RequestBody Communique communique) {
        if (communique.getEtablissementId() != null) {
            etablissementRepository.findById(communique.getEtablissementId())
                    .ifPresent(e -> communique.setEtablissementNom(e.getNom()));
        }
        return repository.save(communique);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}