import java.util.Properties
import java.io.FileInputStream

// Las propiedades se leerán directamente
val envProperties = Properties()
val envFile = project.rootDir.resolve(".env.local")
if (envFile.exists()) {
    envProperties.load(FileInputStream(envFile))
}
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.adobe.marketing.mobile.messagingsample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.adobe.marketing.mobile.messagingsample"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // --- INYECCIÓN DE SECRETOS ---
        val adobeAppId = envProperties.getProperty("ADOBE_APP_ID", "").trim().removeSurrounding("\"")
        val adobeAssuranceSessionId = envProperties.getProperty("ADOBE_ASSURANCE_SESSION_ID", "").trim().removeSurrounding("\"")
        
        buildConfigField("String", "ADOBE_APP_ID", "\"$adobeAppId\"")
        buildConfigField("String", "ADOBE_ASSURANCE_SESSION_ID", "\"$adobeAssuranceSessionId\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation("com.adobe.marketing.mobile:core:3.5.0")
    implementation("com.adobe.marketing.mobile:edge:3.0.1")
    implementation("com.adobe.marketing.mobile:edgeidentity:3.0.0")
    implementation("com.adobe.marketing.mobile:lifecycle:3.0.1")
    implementation("com.adobe.marketing.mobile:messaging:3.6.0")
    implementation("com.adobe.marketing.mobile:assurance:3.0.1")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
}

tasks.register("generateGoogleServicesJson") {
    doLast {
        val envFile = rootProject.file(".env.local")
        if (envFile.exists()) {
            val fileContent = envFile.readText()
            val marker = "GOOGLE_SERVICES_JSON_CONTENT="
            val startIndex = fileContent.indexOf(marker)
            if (startIndex != -1) {
                var jsonPart = fileContent.substring(startIndex + marker.length).trim()
                val lastBraceIndex = jsonPart.lastIndexOf('}')
                if (lastBraceIndex != -1) {
                    val finalJson = jsonPart.substring(0, lastBraceIndex + 1)
                    val googleServicesJsonFile = file("google-services.json")
                    googleServicesJsonFile.writeText(finalJson)
                    println("SUCCESS: Generated google-services.json from .env.local")
                }
            }
        }
    }
}

tasks.whenTaskAdded {
    if (name.startsWith("process") && name.endsWith("GoogleServices")) {
        dependsOn("generateGoogleServicesJson")
    }
}
