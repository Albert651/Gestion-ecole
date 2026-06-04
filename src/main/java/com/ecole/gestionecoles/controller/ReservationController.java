package com.ecole.gestionecoles.controller;

import com.ecole.gestionecoles.model.Etablissement;
import com.ecole.gestionecoles.model.Reservation;
import com.ecole.gestionecoles.repository.EtablissementRepository;
import com.ecole.gestionecoles.repository.ReservationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Routes : http://localhost:8080/api/reservations
 * Toutes ces routes exigent d'etre connecte (voir SecurityConfig).
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationRepository repository;
    private final EtablissementRepository etablissementRepository;

    public ReservationController(ReservationRepository repository,
                                 EtablissementRepository etablissementRepository) {
        this.repository = repository;
        this.etablissementRepository = etablissementRepository;
    }

    // POST /api/reservations  -> un utilisateur connecte reserve un etablissement
    @PostMapping
    public ResponseEntity<?> reserver(@RequestBody Reservation demande,
                                      @AuthenticationPrincipal UserDetails utilisateur) {
        Optional<Etablissement> etabOpt =
                etablissementRepository.findById(demande.getEtablissementId());
        if (etabOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erreur", "Établissement introuvable."));
        }

        Reservation r = new Reservation();
        // L'email vient du JETON, pas du corps de la requete (securite)
        r.setUtilisateurEmail(utilisateur.getUsername());
        r.setEtablissementId(demande.getEtablissementId());
        r.setEtablissementNom(etabOpt.get().getNom());
        r.setNote(demande.getNote());
        r.setStatut("EN_ATTENTE");

        return ResponseEntity.ok(repository.save(r));
    }

    // GET /api/reservations/mes  -> mes propres reservations
    @GetMapping("/mes")
    public List<Reservation> mesReservations(@AuthenticationPrincipal UserDetails utilisateur) {
        return repository.findByUtilisateurEmailOrderByDateReservationDesc(utilisateur.getUsername());
    }

    // GET /api/reservations  -> toutes les reservations (ADMIN)
    @GetMapping
    public List<Reservation> toutes() {
        return repository.findAllByOrderByDateReservationDesc();
    }

    // PUT /api/reservations/1/statut  -> changer le statut (ADMIN)
    @PutMapping("/{id}/statut")
    public ResponseEntity<?> changerStatut(@PathVariable Long id,
                                           @RequestBody Map<String, String> body) {
        return repository.findById(id).map(r -> {
            r.setStatut(body.getOrDefault("statut", r.getStatut()));
            return ResponseEntity.ok(repository.save(r));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/reservations/1  -> annuler (le proprietaire ou un admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails utilisateur) {
        Optional<Reservation> rOpt = repository.findById(id);
        if (rOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Reservation r = rOpt.get();

        boolean estAdmin = utilisateur.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean estProprietaire = r.getUtilisateurEmail().equals(utilisateur.getUsername());

        if (!estAdmin && !estProprietaire) {
            return ResponseEntity.status(403).build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}