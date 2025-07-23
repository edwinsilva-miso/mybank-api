#!/bin/bash

echo "🔧 Configurando Java 23 para el proyecto MyBank API..."

# Configurar JAVA_HOME para Java 23
export JAVA_HOME=/opt/homebrew/opt/openjdk@23/libexec/openjdk.jdk/Contents/Home
export PATH="/opt/homebrew/opt/openjdk@23/bin:$PATH"

# Verificar la versión de Java
echo "✅ Java version configurada:"
java -version

# Agregar configuración permanente al .zshrc
if ! grep -q "JAVA_HOME.*openjdk@23" ~/.zshrc; then
    echo "" >> ~/.zshrc
    echo "# Java 23 configuration for MyBank API" >> ~/.zshrc
    echo "export JAVA_HOME=/opt/homebrew/opt/openjdk@23/libexec/openjdk.jdk/Contents/Home" >> ~/.zshrc
    echo "export PATH=\"/opt/homebrew/opt/openjdk@23/bin:\$PATH\"" >> ~/.zshrc
    echo "✅ Configuración agregada a ~/.zshrc"
else
    echo "ℹ️  Java 23 ya está configurado en ~/.zshrc"
fi

echo "🎉 Java 23 configurado exitosamente!"
echo "💡 Para aplicar los cambios en nuevas terminales, ejecuta: source ~/.zshrc" 