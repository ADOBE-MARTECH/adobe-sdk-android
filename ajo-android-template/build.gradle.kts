// Este es el archivo de compilación de la raíz del proyecto.
// Su principal responsabilidad es definir las versiones de los plugins
// que usarán todos los módulos del proyecto.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Aquí definimos la versión del Android Gradle Plugin (AGP).
        // Mantener esto actualizado es clave.
        classpath("com.android.tools.build:gradle:8.2.2")

        // El plugin de Kotlin para Android.
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")

        // Google Services plugin
        classpath("com.google.gms:google-services:4.4.1")
    }
}