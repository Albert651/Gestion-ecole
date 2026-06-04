package com.ecole.gestionecoles.service;

import com.ecole.gestionecoles.model.Message;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service d'envoi d'e-mails. On envoie une version TEXTE + une version HTML
 * (e-mail "multipart/alternative") : c'est ce que font les e-mails legitimes,
 * ce qui reduit le risque de classement en spam. Le logo est embarque.
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

    public void envoyerMessageContact(Message message, String emailDirecteur, String nomEtablissement) {
        try {
            List<String> destinataires = new ArrayList<>();
            destinataires.add(emailAdmin);
            if (emailDirecteur != null && !emailDirecteur.isBlank()
                    && !emailDirecteur.equalsIgnoreCase(emailAdmin)) {
                destinataires.add(emailDirecteur);
            }

            MimeMessage mime = mailSender.createMimeMessage();
            // MULTIPART_MODE_MIXED_RELATED : autorise texte+html ET image embarquee
            MimeMessageHelper helper =
                    new MimeMessageHelper(mime, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setFrom(compteGmail, nomSite);
            helper.setTo(destinataires.toArray(new String[0]));
            helper.setReplyTo(message.getEmail());

            String sujet = "Nouveau message de contact";
            if (nomEtablissement != null) {
                sujet += " — " + nomEtablissement;
            }
            helper.setSubject(sujet);

            ClassPathResource logo = new ClassPathResource("logo.png");
            boolean logoPresent = logo.exists();

            // setText(texteBrut, html) : Spring cree un e-mail multipart/alternative.
            // Le client e-mail affiche le HTML, mais le texte sert de repli.
            helper.setText(
                    construireTexte(message, nomEtablissement),
                    construireHtml(message, nomEtablissement, logoPresent)
            );

            if (logoPresent) {
                helper.addInline("logoSite", logo);
            }

            mailSender.send(mime);
        } catch (Exception e) {
            System.err.println("Echec de l'envoi de l'e-mail : " + e.getMessage());
        }
    }

    // Version texte brut (repli, et signal de "vrai" e-mail)
    private String construireTexte(Message message, String nomEtablissement) {
        return "Vous avez recu un nouveau message via le site " + nomSite + ".\n\n"
                + "De : " + message.getNom() + "\n"
                + "E-mail : " + message.getEmail() + "\n"
                + (nomEtablissement != null ? "Etablissement : " + nomEtablissement + "\n" : "")
                + "\nMessage :\n" + message.getContenu() + "\n";
    }

    private String construireHtml(Message message, String nomEtablissement, boolean logoPresent) {
        String enTete = logoPresent
                ? "<img src=\"cid:logoSite\" alt=\"" + echapper(nomSite) + "\" style=\"max-height:56px;\"/>"
                : "<span style=\"font-size:18px;font-weight:bold;color:#14213d;\">Établissements "
                  + "<span style=\"color:#c8a24b;\">Scolaires</span></span>";

        return ""
                + "<div style=\"font-family:Arial,Helvetica,sans-serif;max-width:560px;margin:auto;"
                +   "border:1px solid #eee;border-radius:12px;overflow:hidden;\">"
                +   "<div style=\"background:#ffffff;padding:20px 24px;border-bottom:3px solid #c8a24b;"
                +     "text-align:center;\">" + enTete + "</div>"
                +   "<div style=\"padding:24px;color:#33415c;line-height:1.5;\">"
                +     "<p>Vous avez reçu un nouveau message via le site.</p>"
                +     "<p><strong>De :</strong> " + echapper(message.getNom()) + "<br/>"
                +        "<strong>E-mail :</strong> " + echapper(message.getEmail()) + "<br/>"
                +        (nomEtablissement != null
                ? "<strong>Établissement :</strong> " + echapper(nomEtablissement) + "<br/>"
                : "")
                +     "</p>"
                +     "<div style=\"background:#faf7f0;border-left:3px solid #c8a24b;"
                +        "padding:14px 18px;border-radius:6px;\">"
                +        echapper(message.getContenu()).replace("\n", "<br/>")
                +     "</div>"
                +   "</div>"
                + "</div>";
    }

    private String echapper(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}