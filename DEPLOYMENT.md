# 🚀 Guide de Déploiement

## Configuration pour la Production

### 1. Configuration PostgreSQL

Créez un fichier `application-prod.properties`:

```properties
# Application Name
spring.application.name=navi-education

# Server Configuration
server.port=8080

# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/navi_education
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Logging
logging.level.com.navi.education=INFO
logging.level.org.springframework.web=WARN
logging.level.org.hibernate.SQL=WARN

# Production Settings
spring.thymeleaf.cache=true
```

### 2. Créer la base de données PostgreSQL

```sql
CREATE DATABASE navi_education;
CREATE USER navi_user WITH ENCRYPTED PASSWORD 'votre_mot_de_passe';
GRANT ALL PRIVILEGES ON DATABASE navi_education TO navi_user;
```

### 3. Variables d'environnement

Créez un fichier `.env`:

```bash
DB_USERNAME=navi_user
DB_PASSWORD=votre_mot_de_passe
SPRING_PROFILES_ACTIVE=prod
```

### 4. Build de l'application

```bash
mvn clean package -DskipTests
```

Le fichier JAR sera généré dans `target/navi-education-1.0.0.jar`

### 5. Lancement en production

**Option 1: Directement avec Java**
```bash
java -jar target/navi-education-1.0.0.jar --spring.profiles.active=prod
```

**Option 2: Avec variables d'environnement**
```bash
export DB_USERNAME=navi_user
export DB_PASSWORD=votre_mot_de_passe
export SPRING_PROFILES_ACTIVE=prod
java -jar target/navi-education-1.0.0.jar
```

**Option 3: Service systemd**

Créez `/etc/systemd/system/navi-education.service`:

```ini
[Unit]
Description=Navi Education Management System
After=syslog.target network.target

[Service]
User=navi
WorkingDirectory=/opt/navi-education
ExecStart=/usr/bin/java -jar /opt/navi-education/navi-education-1.0.0.jar
SuccessExitStatus=143
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_USERNAME=navi_user"
Environment="DB_PASSWORD=votre_mot_de_passe"

[Install]
WantedBy=multi-user.target
```

Ensuite:
```bash
sudo systemctl daemon-reload
sudo systemctl enable navi-education
sudo systemctl start navi-education
sudo systemctl status navi-education
```

### 6. Configuration Nginx (Reverse Proxy)

Créez `/etc/nginx/sites-available/navi-education`:

```nginx
server {
    listen 80;
    server_name votre-domaine.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Activez le site:
```bash
sudo ln -s /etc/nginx/sites-available/navi-education /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 7. SSL avec Let's Encrypt

```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d votre-domaine.com
```

## Docker Deployment

### Dockerfile

Créez un `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=target/navi-education-1.0.0.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: navi_education
      POSTGRES_USER: navi_user
      POSTGRES_PASSWORD: votre_mot_de_passe
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_USERNAME: navi_user
      DB_PASSWORD: votre_mot_de_passe
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/navi_education
    depends_on:
      - postgres

volumes:
  postgres_data:
```

Lancement avec Docker:
```bash
docker-compose up -d
```

## Sauvegardes

### Sauvegarde PostgreSQL

```bash
# Backup
pg_dump -U navi_user -h localhost navi_education > backup_$(date +%Y%m%d).sql

# Restore
psql -U navi_user -h localhost navi_education < backup_20240101.sql
```

### Script de sauvegarde automatique (cron)

```bash
#!/bin/bash
BACKUP_DIR="/var/backups/navi-education"
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump -U navi_user -h localhost navi_education | gzip > $BACKUP_DIR/backup_$DATE.sql.gz
# Garder seulement les 30 derniers backups
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +30 -delete
```

Ajoutez au crontab:
```bash
0 2 * * * /path/to/backup-script.sh
```

## Monitoring

### Logs

```bash
# Voir les logs
sudo journalctl -u navi-education -f

# Logs de l'application
tail -f /var/log/navi-education/application.log
```

### Health Check

Vérifiez que l'application fonctionne:
```bash
curl http://localhost:8080/api/branches
```

## Sécurité

1. **Ne jamais exposer la console H2 en production**
2. **Utiliser HTTPS en production**
3. **Protéger les endpoints sensibles avec Spring Security** (à ajouter)
4. **Utiliser des variables d'environnement pour les secrets**
5. **Configurer un pare-feu (ufw/firewalld)**

```bash
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

## Mise à jour

1. **Sauvegarder la base de données**
2. **Arrêter l'application**
   ```bash
   sudo systemctl stop navi-education
   ```
3. **Remplacer le JAR**
   ```bash
   cp target/navi-education-1.0.0.jar /opt/navi-education/
   ```
4. **Redémarrer l'application**
   ```bash
   sudo systemctl start navi-education
   ```
5. **Vérifier les logs**
   ```bash
   sudo journalctl -u navi-education -f
   ```

## Troubleshooting

### L'application ne démarre pas

1. Vérifier les logs: `sudo journalctl -u navi-education -n 100`
2. Vérifier la connexion à PostgreSQL
3. Vérifier les permissions sur le fichier JAR
4. Vérifier les variables d'environnement

### Erreurs de base de données

1. Vérifier que PostgreSQL est en cours d'exécution
2. Vérifier les credentials
3. Vérifier que l'utilisateur a les bonnes permissions

### Performance lente

1. Augmenter la mémoire JVM: `-Xmx2g -Xms1g`
2. Optimiser les requêtes SQL
3. Ajouter des index sur la base de données
4. Configurer un pool de connexions

## Support

Pour toute question ou problème, consultez les logs et la documentation.
