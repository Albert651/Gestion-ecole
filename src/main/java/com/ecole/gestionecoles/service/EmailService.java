package com.ecole.gestionecoles.service;

import com.ecole.gestionecoles.model.Message;
import com.ecole.gestionecoles.model.Reservation;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service d'envoi d'e-mails (HTML), avec logo embarque.
 * - envoyerMessageContact : formulaire de contact -> admin + directeur
 * - envoyerNotificationReservation : changement de statut -> utilisateur
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String compteGmail;

    @Value("${app.admin-email}")
    private String emailAdmin;

    @Value("${app.site-name:Établissements Scolaires}")
    private String nomSite;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ============================================================
    //  1) Message du formulaire de contact (vers admin + directeur)
    // ============================================================
    public void envoyerMessageContact(Message message, String emailDirecteur, String nomEtablissement) {
        try {
            List<String> destinataires = new ArrayList<>();
            destinataires.add(emailAdmin);
            if (emailDirecteur != null && !emailDirecteur.isBlank()
                    && !emailDirecteur.equalsIgnoreCase(emailAdmin)) {
                destinataires.add(emailDirecteur);
            }

            String sujet = "Nouveau message de contact"
                    + (nomEtablissement != null ? " — " + nomEtablissement : "");

            String corpsTexte = "Vous avez recu un nouveau message via le site " + nomSite + ".\n\n"
                    + "De : " + message.getNom() + "\n"
                    + "E-mail : " + message.getEmail() + "\n"
                    + (nomEtablissement != null ? "Etablissement : " + nomEtablissement + "\n" : "")
                    + "\nMessage :\n" + message.getContenu() + "\n";

            String corpsHtmlInterieur =
                    "<p>Vous avez reçu un nouveau message via le site.</p>"
                            + "<p><strong>De :</strong> " + echapper(message.getNom()) + "<br/>"
                            + "<strong>E-mail :</strong> " + echapper(message.getEmail()) + "<br/>"
                            + (nomEtablissement != null
                            ? "<strong>Établissement :</strong> " + echapper(nomEtablissement) + "<br/>" : "")
                            + "</p>"
                            + "<div style=\"background:#faf7f0;border-left:3px solid #c8a24b;padding:14px 18px;border-radius:6px;\">"
                            + echapper(message.getContenu()).replace("\n", "<br/>")
                            + "</div>";

            envoyer(destinataires, message.getEmail(), sujet, corpsTexte, corpsHtmlInterieur);
        } catch (Exception e) {
            System.err.println("Echec de l'envoi de l'e-mail (contact) : " + e.getMessage());
        }
    }

    // ============================================================
    //  2) Notification de reservation (vers l'utilisateur)
    // ============================================================
    public void envoyerNotificationReservation(Reservation r) {
        try {
            String statut = r.getStatut();
            String etab = r.getEtablissementNom();

            String titre;
            String phrase;
            if ("CONFIRMEE".equals(statut)) {
                titre = "Réservation confirmée 🎉";
                phrase = "Bonne nouvelle ! Votre réservation pour <strong>" + echapper(etab)
                        + "</strong> a été <strong>confirmée</strong>.";
            } else if ("ANNULEE".equals(statut)) {
                titre = "Réservation non retenue";
                phrase = "Votre réservation pour <strong>" + echapper(etab)
                        + "</strong> n'a pas pu être confirmée. N'hésitez pas à nous contacter pour plus d'informations.";
            } else {
                titre = "Mise à jour de votre réservation";
                phrase = "Le statut de votre réservation pour <strong>" + echapper(etab)
                        + "</strong> est désormais : <strong>" + echapper(statut) + "</strong>.";
            }

            String sujet = "Votre réservation — " + etab;

            String corpsTexte = "Bonjour,\n\n"
                    + "Statut de votre reservation pour " + etab + " : " + statut + ".\n\n"
                    + "Merci de votre confiance,\n" + nomSite + "\n";

            String corpsHtmlInterieur =
                    "<p style=\"font-size:18px;font-weight:bold;color:#14213d;\">" + titre + "</p>"
                            + "<p>" + phrase + "</p>"
                            + "<p style=\"color:#8a8a8a;font-size:13px;margin-top:18px;\">Merci de votre confiance, "
                            + echapper(nomSite) + ".</p>";

            envoyer(List.of(r.getUtilisateurEmail()), emailAdmin, sujet, corpsTexte, corpsHtmlInterieur);
        } catch (Exception e) {
            System.err.println("Echec de l'envoi de l'e-mail (reservation) : " + e.getMessage());
        }
    }

    // ============================================================
    //  Methode commune d'envoi (texte + HTML + logo embarque)
    // ============================================================
    private void envoyer(List<String> destinataires, String replyTo,
                         String sujet, String corpsTexte, String corpsHtmlInterieur) throws Exception {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(mime, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        helper.setFrom(compteGmail, nomSite);
        helper.setTo(destinataires.toArray(new String[0]));
        if (replyTo != null && !replyTo.isBlank()) {
            helper.setReplyTo(replyTo);
        }
        helper.setSubject(sujet);

        ClassPathResource logo = new ClassPathResource("logo.png");
        boolean logoPresent = logo.exists();

        helper.setText(corpsTexte, envelopperHtml(corpsHtmlInterieur, logoPresent));
        if (logoPresent) {
            helper.addInline("logoSite", logo);
        }

        mailSender.send(mime);
    }

    // Habillage commun (en-tete avec logo + cadre)
    private String envelopperHtml(String interieur, boolean logoPresent) {
        String enTete = logoPresent
                ? "<img src=\"cid:logoSite\" alt=\"" + echapper(nomSite) + "\" style=\"max-height:56px;\"/>"
                : "<span style=\"font-size:18px;font-weight:bold;color:#14213d;\">Établissements "
                  + "<span style=\"color:#c8a24b;\">Scolaires</span></span>";

        return "<div style=\"font-family:Arial,Helvetica,sans-serif;max-width:560px;margin:auto;"
                + "border:1px solid #eee;border-radius:12px;overflow:hidden;\">"
                + "<div style=\"background:#ffffff;padding:20px 24px;border-bottom:3px solid #c8a24b;text-align:center;\">"
                + enTete + "</div>"
                + "<div style=\"padding:24px;color:#33415c;line-height:1.5;\">" + interieur + "</div>"
                + "</div>";
    }

    private String echapper(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}