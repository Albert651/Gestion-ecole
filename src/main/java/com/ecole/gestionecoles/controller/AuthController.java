package com.ecole.gestionecoles.controller;

import com.ecole.gestionecoles.model.Utilisateur;
import com.ecole.gestionecoles.repository.UtilisateurRepository;
import com.ecole.gestionecoles.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Routes : http://localhost:8080/api/auth
 * - POST /register  : creer un compte (role USER)
 * - POST /login     : se connecter et recevoir un jeton JWT
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilisateurRepository repository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthController(UtilisateurRepository repository,
                          PasswordEncoder encoder,
                          AuthenticationManager authManager,
                          JwtService jwtService) {
        this.repository = repository;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    // ----- Inscription -----
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody InscriptionRequete req) {
        if (repository.existsByEmail(req.email())) {
            return ResponseEntity.badRequest().body(Map.of("erreur", "Cet e-mail est déjà utilisé."));
        }
        Utilisateur u = new Utilisateur();
        u.setNom(req.nom());
        u.setEmail(req.email());
        u.setMotDePasse(encoder.encode(req.motDePasse())); // on hache le mot de passe
        u.setRole("USER");
        repository.save(u);

        String jeton = jwtService.genererJeton(u.getEmail(), u.getRole());
        return ResponseEntity.ok(new AuthReponse(jeton, u.getNom(), u.getEmail(), u.getRole()));
    }

    // ----- Connexion -----
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ConnexionRequete req) {
        try {
            // Verifie email + mot de passe ; leve une exception si incorrect
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.motDePasse()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("erreur", "E-mail ou mot de passe incorrect."));
        }

        Utilisateur u = repository.findByEmail(req.email()).orElseThrow();
        String jeton = jwtService.genererJeton(u.getEmail(), u.getRole());
        return ResponseEntity.ok(new AuthReponse(jeton, u.getNom(), u.getEmail(), u.getRole()));
    }

    // ----- Petits "formats" de donnees (records) -----
    public record InscriptionRequete(String nom, String email, String motDePasse) {}
    public record ConnexionRequete(String email, String motDePasse) {}
    public record AuthReponse(String token, String nom, String email, String role) {}
}