import java.io.File

// Este archivo define la configuración a nivel de todo el proyecto.

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Nombre de la raíz del proyecto.
rootProject.name = "AJO Android Template"

// Incluimos nuestro único módulo de aplicación, llamado "app".
include(":app")
