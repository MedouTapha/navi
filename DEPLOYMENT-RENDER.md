# 🚀 Déploiement sur Render (gratuit, sans VPS)

Render héberge l'application **et** la base PostgreSQL, et redéploie
automatiquement à chaque `git push`. Tout est décrit par le fichier
[`render.yaml`](./render.yaml) à la racine du dépôt.

> 💡 **Bon à savoir (offre gratuite Render)**
> - Le service web s'**endort après 15 min** sans visite ; la 1ʳᵉ requête
>   suivante prend ~50 s (le temps du réveil), puis c'est rapide.
> - La base PostgreSQL gratuite **expire après ~30 jours**. Pour un usage
>   durable, passez la base en plan payant (~7 $/mois) ou utilisez une base
>   Neon gratuite (voir la fin du document).

---

## Étapes (≈ 10 minutes)

### 1. Pousser le code sur GitHub
Le dépôt doit être sur GitHub (déjà le cas ici : `MedouTapha/navi`).
Assurez-vous que `Dockerfile` et `render.yaml` sont bien présents à la racine.

### 2. Créer un compte Render
Aller sur **https://render.com** → *Get Started* → se connecter **avec GitHub**
(gratuit, sans carte bancaire).

### 3. Déployer le Blueprint
1. Dans le tableau de bord Render : **New + → Blueprint**.
2. Sélectionner le dépôt **`MedouTapha/navi`** (autoriser Render à y accéder).
3. Render lit automatiquement `render.yaml` et propose de créer :
   - le service web **`navi-education`** (depuis le `Dockerfile`)
   - la base **`navi-db`** (PostgreSQL, plan gratuit)
4. Render demande la valeur du secret **`ADMIN_PASSWORD`** → saisir un mot de
   passe administrateur fort (c'est celui de la 1ʳᵉ connexion).
5. Cliquer **Apply**.

### 4. Attendre le build
Render construit l'image Docker (≈ 3-5 min la première fois), puis démarre.
La base est câblée automatiquement (variables `DB_HOST`, `DB_PORT`, `DB_NAME`,
`DB_USERNAME`, `DB_PASSWORD` injectées par `render.yaml`).

### 5. Ouvrir l'application
L'URL publique apparaît en haut de la page du service, par ex. :
```
https://navi-education.onrender.com
```
Se connecter avec :
- **Identifiant** : `admin`
- **Mot de passe** : celui saisi à l'étape 3.4

✅ L'application est en ligne et accessible depuis n'importe où, en HTTPS.

---

## 🔄 Mettre à jour l'application
Il suffit de pousser sur la branche déployée :
```bash
git push
```
Render rebuild et redéploie automatiquement.

---

## 🔐 Changer le mot de passe admin plus tard
Tableau de bord Render → service `navi-education` → **Environment** →
modifier `ADMIN_PASSWORD`.
⚠️ Le compte admin n'est créé qu'au **premier** démarrage (base vide). Si le
compte existe déjà, changez le mot de passe via PostgreSQL ou recréez la base.

---

## 💾 Garder les données durablement (base gratuite persistante : Neon)
La base gratuite de Render expirant après ~30 jours, pour un usage réel :

**Option A — Base Render payante** : sur `navi-db`, passer au plan **Starter**.

**Option B — Base Neon gratuite et persistante** :
1. Créer une base sur **https://neon.tech** (gratuit, ne s'expire pas).
2. Neon fournit un *host*, *database*, *user*, *password*.
3. Dans `render.yaml`, supprimer la section `databases:` et les 5 blocs
   `fromDatabase`, puis définir manuellement dans **Environment** :
   `DB_HOST`, `DB_PORT` (souvent `5432`), `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
   (valeurs Neon). Ajouter `?sslmode=require` est géré par Neon côté hôte.

---

## ❓ En cas d'erreur
- **Build échoue** : vérifier les logs *Build* du service. Le `Dockerfile`
  compile avec Maven (Java 17) — une erreur de compilation y apparaîtra.
- **App démarre puis crash** : onglet *Logs*. Une erreur de connexion DB
  (`HikariPool ... refused`) signifie que les variables `DB_*` ne sont pas
  câblées — vérifier que la base `navi-db` est bien *Available*.
- **Page lente au premier accès** : normal (réveil après mise en veille).
