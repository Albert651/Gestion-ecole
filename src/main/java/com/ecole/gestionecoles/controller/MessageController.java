package com.ecole.gestionecoles.controller;

import com.ecole.gestionecoles.model.Etablissement;
import com.ecole.gestionecoles.model.Message;
import com.ecole.gestionecoles.repository.EtablissementRepository;
import com.ecole.gestionecoles.repository.MessageRepository;
import com.ecole.gestionecoles.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Routes : http://localhost:8080/api/messages
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository repository;
    private final EtablissementRepository etablissementRepository;
    private final EmailService emailService;

    public MessageController(MessageRepository repository,
                             EtablissementRepository etablissementRepository,
                             EmailService emailService) {
        this.repository = repository;
        this.etablissementRepository = etablissementRepository;
        this.emailService = emailService;
    }

    // GET /api/messages  -> tous les messages recus (pour l'admin)
    @GetMapping
    public List<Message> listerTous() {
        return repository.findAllByOrderByDateEnvoiDesc();
    }

    // POST /api/messages  -> un visiteur envoie un message
    @PostMapping
    public Message envoyer(@RequestBody Message message) {
        // 1. On enregistre le message en base
        Message enregistre = repository.save(message);

        // 2. On cherche l'e-mail du directeur si le message concerne un etablissement
        String emailDirecteur = null;
        String nomEtablissement = null;
        if (message.getEtablissementId() != null) {
            Optional<Etablissement> etabOpt =
                    etablissementRepository.findById(message.getEtablissementId());
            if (etabOpt.isPresent()) {
                emailDirecteur = etabOpt.get().getEmail();
                nomEtablissement = etabOpt.get().getNom();
            }
        }

        // 3. On envoie l'e-mail (a l'admin + au directeur)
        emailService.envoyerMessageContact(enregistre, emailDirecteur, nomEtablissement);

        return enregistre;
    }

    // DELETE /api/messages/1  -> l'admin supprime un message
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}