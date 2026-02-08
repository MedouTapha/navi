#!/bin/bash

# Script de vérification de la configuration Java pour Navi

echo "=========================================="
echo "   Vérification Configuration Java"
echo "=========================================="
echo ""

echo "📌 Version de Java (java -version):"
java -version
echo ""

echo "📌 Version du compilateur Java (javac -version):"
javac -version
echo ""

echo "📌 Version de Maven (mvn -version):"
mvn -version
echo ""

echo "📌 Variable JAVA_HOME:"
if [ -z "$JAVA_HOME" ]; then
    echo "❌ JAVA_HOME n'est pas définie"
else
    echo "✅ JAVA_HOME = $JAVA_HOME"
    echo "   Version: $($JAVA_HOME/bin/java -version 2>&1 | head -n 1)"
fi
echo ""

echo "📌 Vérification du pom.xml:"
if grep -q "<java.version>17</java.version>" pom.xml; then
    echo "✅ pom.xml utilise Java 17"
else
    echo "❌ pom.xml n'utilise pas Java 17"
    echo "   Version trouvée: $(grep "<java.version>" pom.xml)"
fi
echo ""

echo "=========================================="
echo "   Recommandations"
echo "=========================================="
echo ""
echo "✅ Versions recommandées:"
echo "   - Java: 17.x (LTS)"
echo "   - Maven: 3.6.x ou supérieur"
echo ""
echo "⚠️  Versions NON supportées:"
echo "   - Java 24 (incompatible avec Spring Boot 3.2.1)"
echo "   - Java 23 (non testé)"
echo "   - Java < 17 (trop ancien)"
echo ""

echo "📚 Pour plus d'aide, consultez: INTELLIJ_SETUP.md"
