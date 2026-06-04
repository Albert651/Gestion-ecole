# Gestion des établissements scolaires — Backend (Spring Boot)

API REST en Java Spring Boot, connectée à une base PostgreSQL hébergée sur Supabase.

---

## 1. Prérequis (déjà installés chez toi ✅)

- Java 17
- Maven
- Un compte Supabase avec un projet créé

---

## 2. Brancher Supabase (étape importante)

1. Va sur ton tableau de bord Supabase → ton projet.
2. Clique sur le bouton **« Connect »** (en haut), onglet **ORMs** ou **JDBC**,
   OU va dans **Project Settings → Database**.
3. Choisis le mode **« Session pooler »** (recommandé : il fonctionne en IPv4
   et est compatible avec Hibernate). Tu obtiens 3 infos :

   - **Host** → quelque chose comme `aws-0-eu-west-3.pooler.supabase.com`
   - **User** → quelque chose comme `postgres.abcdefghijklmnop`
   - **Password** → le mot de passe choisi à la création du projet

4. Ouvre le fichier `src/main/resources/application.properties` et remplace :

   ```
   spring.datasource.url=jdbc:postgresql://aws-0-eu-west-3.pooler.supabase.com:5432/postgres
   spring.datasource.username=postgres.abcdefghijklmnop
   spring.datasource.password=TON_MOT_DE_PASSE
   ```

> ⚠️ Ne mets PAS le mode « Transaction pooler » (port 6543) : il pose problème
> avec Hibernate. Reste sur **Session pooler** ou la connexion directe.

---

## 3. Lancer le projet

Dans un terminal, à la racine du dossier `gestion-ecoles` :

```bash
mvn spring-boot:run
```

(La première fois, Maven télécharge les dépendances : patiente quelques minutes.)

Si tu vois `Started GestionEcolesApplication in X seconds`, c'est gagné ! 🚀
Le serveur tourne sur http://localhost:8080

Grâce à `ddl-auto=update`, la table `etablissements` est créée
automatiquement dans Supabase au démarrage. Va vérifier dans
Supabase → **Table Editor**, tu devrais la voir apparaître.

---

## 4. Tester l'API

**Lister les établissements** (vide au début) — dans le navigateur :
```
http://localhost:8080/api/etablissements
```

**Créer un établissement** — avec curl ou Postman :
```bash
curl -X POST http://localhost:8080/api/etablissements \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Lycée Excellence",
    "description": "Un établissement de référence.",
    "adresse": "123 rue de l école",
    "caracteristiques": "Laboratoire moderne, internat, terrains de sport",
    "email": "directeur@excellence.edu",
    "telephone": "+261 34 00 000 00"
  }'
```

Recharge ensuite `http://localhost:8080/api/etablissements` : ton établissement apparaît.

---

## 5. Les routes disponibles

| Méthode | URL                          | Rôle                          |
|---------|------------------------------|-------------------------------|
| GET     | /api/etablissements          | Lister tous les établissements|
| GET     | /api/etablissements/{id}     | Détail d'un établissement     |
| POST    | /api/etablissements          | Créer un établissement        |
| PUT     | /api/etablissements/{id}     | Modifier un établissement     |
| DELETE  | /api/etablissements/{id}     | Supprimer un établissement    |

---

## 6. Structure du projet

```
src/main/java/com/ecole/gestionecoles/
├── GestionEcolesApplication.java   → démarre l'application
├── model/Etablissement.java        → la "forme" d'un établissement (= une table)
├── repository/EtablissementRepository.java → accès base de données (CRUD auto)
├── controller/EtablissementController.java → les routes de l'API
└── config/CorsConfig.java          → autorise React à appeler l'API
```

**Pour ajouter une nouvelle fonctionnalité** (Annonce, Communiqué...), tu copies
ce schéma : 1 model + 1 repository + 1 controller. C'est toujours le même motif.
