package com.ecole.gestionecoles.controller;

import com.ecole.gestionecoles.model.Annonce;
import com.ecole.gestionecoles.repository.AnnonceRepository;
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
 * Routes : http://localhost:8080/api/annonces
 */
@RestController
@RequestMapping("/api/annonces")
public class AnnonceController {

    private final AnnonceRepository repository;

    public AnnonceController(AnnonceRepository repository) {
        this.repository = repository;
    }

    // GET /api/annonces  -> toutes les annonces, plus recentes en premier
    @GetMapping
    public List<Annonce> listerTous() {
        return repository.findAllByOrderByDatePublicationDesc();
    }

    // POST /api/annonces  -> creer une annonce
    @PostMapping
    public Annonce creer(@RequestBody Annonce annonce) {
        return repository.save(annonce);
    }

    // DELETE /api/annonces/1  -> supprimer une annonce
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
