# 🚀 Guide de déploiement — Navi Education (VPS Ubuntu)

Déploiement de l'application **معهد الإمام نافع** sur un serveur Ubuntu (VPS), en
service `systemd`, avec **PostgreSQL** et **HTTPS** complet.

> Testé sur Ubuntu 22.04 / 24.04 LTS. Adaptez les noms de domaine et mots de passe.

---

## 0. Vue d'ensemble de l'architecture

```
Internet ──HTTPS──> Nginx (443) ──proxy──> Spring Boot (127.0.0.1:8080) ──> PostgreSQL (5432)
                      │
                  Certbot / Let's Encrypt
```

- L'application Java **n'est jamais exposée directement** : seul Nginx écoute sur Internet.
- Spring Security protège **toutes** les pages (login obligatoire).
- Les secrets (mot de passe DB, admin) sont dans `/etc/navi-education/navi.env` (chmod 600).

---

## 1. Préparer le serveur

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y openjdk-17-jre-headless postgresql nginx git
```

Vérifier Java :
```bash
java -version   # doit afficher 17.x
```

---

## 2. Configurer PostgreSQL

```bash
sudo -u postgres psql
```

Dans l'invite `psql` :
```sql
CREATE DATABASE navi_education;
CREATE USER navi_user WITH ENCRYPTED PASSWORD 'mot_de_passe_db_fort';
GRANT ALL PRIVILEGES ON DATABASE navi_education TO navi_user;
-- PostgreSQL 15+ : donner les droits sur le schéma public
\c navi_education
GRANT ALL ON SCHEMA public TO navi_user;
\q
```

---

## 3. Créer l'utilisateur système et les dossiers

```bash
sudo useradd -r -s /usr/sbin/nologin navi
sudo mkdir -p /opt/navi-education
sudo mkdir -p /etc/navi-education
```

---

## 4. Construire le JAR

**Sur votre machine de développement** (avec accès Internet pour Maven) :
```bash
cd navi
./mvnw clean package -DskipTests    # ou : mvn clean package -DskipTests
```
Le JAR est généré dans `target/navi-education-1.0.0.jar`.

Copier le JAR sur le serveur :
```bash
scp target/navi-education-1.0.0.jar user@VOTRE_SERVEUR:/tmp/
```
Puis sur le serveur :
```bash
sudo mv /tmp/navi-education-1.0.0.jar /opt/navi-education/navi-education.jar
sudo chown -R navi:navi /opt/navi-education
```

---

## 5. Configurer les variables d'environnement (secrets)

```bash
sudo cp deploy/navi.env.example /etc/navi-education/navi.env
sudo nano /etc/navi-education/navi.env     # éditez les mots de passe
sudo chmod 600 /etc/navi-education/navi.env
sudo chown root:navi /etc/navi-education/navi.env
```

Renseignez **obligatoirement** :
- `DB_PASSWORD` → le mot de passe créé à l'étape 2
- `ADMIN_PASSWORD` → un mot de passe fort pour le compte administrateur initial

---

## 6. Installer le service systemd

```bash
sudo cp deploy/navi-education.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable navi-education
sudo systemctl start navi-education
```

Vérifier l'état et les logs :
```bash
sudo systemctl status navi-education
sudo journalctl -u navi-education -f
```

L'application répond maintenant en local sur `http://127.0.0.1:8080`.

---

## 7. Configurer Nginx (reverse-proxy)

```bash
sudo cp deploy/nginx-navi-education.conf /etc/nginx/sites-available/navi-education
sudo nano /etc/nginx/sites-available/navi-education   # remplacez votre-domaine.com
sudo ln -s /etc/nginx/sites-available/navi-education /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t && sudo systemctl reload nginx
```

---

## 8. Activer HTTPS (Let's Encrypt)

```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d votre-domaine.com
```
Certbot configure automatiquement le certificat + la redirection HTTP→HTTPS.
Le renouvellement est automatique (timer systemd `certbot.timer`).

---

## 9. Pare-feu (UFW)

```bash
sudo ufw allow OpenSSH
sudo ufw allow 'Nginx Full'
sudo ufw enable
```
> PostgreSQL (5432) et l'app (8080) restent **uniquement** accessibles en local —
> ne pas les ouvrir vers l'extérieur.

---

## 10. Première connexion

Ouvrez `https://votre-domaine.com` → page de login.
Connectez-vous avec `ADMIN_USERNAME` / `ADMIN_PASSWORD` définis à l'étape 5.

---

## 🔄 Mettre à jour l'application

```bash
# 1. Reconstruire le JAR en local, le copier dans /tmp sur le serveur
sudo systemctl stop navi-education
sudo mv /tmp/navi-education-1.0.0.jar /opt/navi-education/navi-education.jar
sudo chown navi:navi /opt/navi-education/navi-education.jar
sudo systemctl start navi-education
```
Les données PostgreSQL sont conservées (le schéma est mis à jour automatiquement
par Hibernate `ddl-auto=update`).

---

## 🔒 Récapitulatif sécurité

| Élément | Mesure |
|---|---|
| Authentification | Spring Security, login obligatoire sur **toutes** les pages |
| Mots de passe | Hachés en **BCrypt** en base |
| CSRF | Activé (formulaires + requêtes fetch protégés) |
| Sessions | Cookies `HttpOnly` + `Secure` + `SameSite=Lax`, expiration 30 min |
| Transport | HTTPS (Let's Encrypt), HTTP redirigé |
| Secrets | Hors du code, dans `navi.env` (chmod 600) |
| Base de données | Accessible uniquement en local, utilisateur dédié |
| Concurrence | Verrouillage optimiste (`@Version`) sur les entités financières |
| Console H2 | **Désactivée** en production |

---

## 💾 Sauvegarde de la base (recommandé)

Sauvegarde quotidienne via cron :
```bash
sudo crontab -e
# Ajouter :
0 2 * * * pg_dump -U navi_user navi_education | gzip > /var/backups/navi_$(date +\%F).sql.gz
```

---

## 🧪 Développement local (rappel)

En local, l'app tourne avec le profil par défaut (H2 en mémoire) :
```bash
./mvnw spring-boot:run
```
Identifiants par défaut en dev : `admin` / `admin123` (modifiable via `ADMIN_USERNAME` / `ADMIN_PASSWORD`).
Accès : http://localhost:8080
