# 🔧 Configuration IntelliJ IDEA pour Navi

## ⚠️ Résolution de l'erreur `ExceptionInInitializerError`

Cette erreur est causée par une incompatibilité entre Java 24 et Spring Boot 3.2.1.

### Solution : Utiliser Java 17

## 📝 Étapes dans IntelliJ IDEA

### 1. Vérifier le JDK du projet

1. **File** → **Project Structure** (ou `Ctrl+Alt+Shift+S`)
2. Sous **Project Settings** → **Project**:
   - **SDK**: Sélectionnez **Java 17** (si absent, cliquez sur "Add SDK" → "Download JDK" → Choisissez Java 17 d'Oracle, Amazon Corretto, ou Azul Zulu)
   - **Language level**: Sélectionnez **17 - Sealed types, always-strict floating-point semantics**
3. Cliquez sur **Apply** puis **OK**

### 2. Vérifier le JDK des modules

1. Toujours dans **Project Structure**
2. Sous **Project Settings** → **Modules**:
3. Sélectionnez votre module `navi-education`
4. Dans l'onglet **Sources**:
   - **Language level**: Devrait être **17**
5. Dans l'onglet **Dependencies**:
   - **Module SDK**: Devrait être **Project SDK (Java 17)**
6. Cliquez sur **Apply** puis **OK**

### 3. Vérifier les paramètres du compilateur Java

1. **File** → **Settings** (ou `Ctrl+Alt+S`)
2. **Build, Execution, Deployment** → **Compiler** → **Java Compiler**
3. Vérifiez que:
   - **Project bytecode version**: **17**
   - **Target bytecode version** pour le module `navi-education`: **17**
4. Cliquez sur **Apply** puis **OK**

### 4. Activer le support Lombok

1. **File** → **Settings** → **Plugins**
2. Recherchez **Lombok**
3. Si le plugin n'est pas installé:
   - Cliquez sur **Install**
   - Redémarrez IntelliJ
4. Ensuite, allez dans **File** → **Settings** → **Build, Execution, Deployment** → **Compiler** → **Annotation Processors**
5. Cochez **Enable annotation processing**
6. Cliquez sur **Apply** puis **OK**

### 5. Recharger le projet Maven

1. Ouvrez l'onglet **Maven** (à droite de l'IDE)
2. Cliquez sur l'icône **Reload All Maven Projects** (icône circulaire avec des flèches)
3. Attendez que toutes les dépendances soient téléchargées

### 6. Nettoyer et recompiler

1. **Build** → **Rebuild Project**
2. Ou via Maven:
   - Ouvrez l'onglet **Maven**
   - Développez **Lifecycle**
   - Double-cliquez sur **clean**
   - Puis double-cliquez sur **install**

### 7. Invalider les caches (si nécessaire)

Si le problème persiste:

1. **File** → **Invalidate Caches**
2. Cochez toutes les options:
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
   - ✅ Clear VCS Log caches and indexes
3. Cliquez sur **Invalidate and Restart**

## 🎯 Configuration Maven en ligne de commande

Si vous préférez compiler en ligne de commande:

```bash
# Nettoyer et compiler
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

## 🔍 Vérifier la version de Java utilisée

### Dans IntelliJ
1. **Help** → **About**
2. Vérifiez la version de la JVM utilisée

### En ligne de commande
```bash
java -version
javac -version
mvn -version
```

Toutes ces commandes devraient afficher **Java 17**.

## ⚡ Lancer l'application dans IntelliJ

### Méthode 1: Via la classe principale

1. Ouvrez `src/main/java/com/navi/education/NaviEducationApplication.java`
2. Cliquez droit sur la classe
3. Sélectionnez **Run 'NaviEducationApplication'**

### Méthode 2: Via Maven

1. Ouvrez l'onglet **Maven**
2. Développez **Plugins** → **spring-boot**
3. Double-cliquez sur **spring-boot:run**

### Méthode 3: Configuration de lancement

1. **Run** → **Edit Configurations**
2. Cliquez sur **+** → **Spring Boot**
3. Configurez:
   - **Name**: Navi Education
   - **Main class**: `com.navi.education.NaviEducationApplication`
   - **JRE**: Java 17
   - **Module**: `navi-education`
4. Cliquez sur **Apply** puis **OK**
5. Lancez avec le bouton ▶️ (Run)

## 📱 Accéder à l'application

Une fois l'application lancée:

- **Interface Web**: http://localhost:8080
- **Console H2**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:navidb`
  - Username: `sa`
  - Password: (vide)

## 🐛 Dépannage

### Erreur: "Cannot resolve symbol..."

1. **File** → **Invalidate Caches** → **Invalidate and Restart**
2. Rechargez le projet Maven

### Erreur: "Lombok annotations not working"

1. Vérifiez que le plugin Lombok est installé
2. Vérifiez que l'annotation processing est activé
3. Redémarrez IntelliJ

### Erreur de compilation avec MapStruct

MapStruct n'est pas utilisé dans le code actuel, vous pouvez le retirer du `pom.xml` si vous avez des problèmes:

Supprimez les dépendances MapStruct (lignes 82-94 du pom.xml)

### Port 8080 déjà utilisé

Si le port 8080 est déjà occupé, modifiez `src/main/resources/application.properties`:

```properties
server.port=8081
```

## ✅ Vérification finale

Après avoir suivi toutes ces étapes, vérifiez que:

1. ✅ Java 17 est configuré partout
2. ✅ Le plugin Lombok est installé et activé
3. ✅ L'annotation processing est activé
4. ✅ Le projet Maven est rechargé
5. ✅ Le projet compile sans erreurs
6. ✅ L'application démarre correctement

## 🆘 Si le problème persiste

Vérifiez dans le terminal d'IntelliJ (en bas) le message d'erreur complet et assurez-vous que:

1. Vous n'avez pas plusieurs versions de Java installées qui causent des conflits
2. La variable d'environnement `JAVA_HOME` pointe vers Java 17
3. IntelliJ utilise le bon JDK

```bash
# Vérifier JAVA_HOME
echo $JAVA_HOME  # Linux/Mac
echo %JAVA_HOME% # Windows

# Devrait pointer vers un dossier Java 17
```

Si vous avez encore des problèmes, partagez le message d'erreur complet.
