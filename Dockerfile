# --- ETAPA 1: Compilación del APK ---
FROM eclipse-temurin:17-jdk-jammy AS builder

ENV ANDROID_SDK_ROOT=/opt/android-sdk
RUN apt-get update && apt-get install -y wget unzip && \
    mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O sdk.zip && \
    unzip sdk.zip -d ${ANDROID_SDK_ROOT}/cmdline-tools && \
    mv ${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools ${ANDROID_SDK_ROOT}/cmdline-tools/latest && \
    rm sdk.zip

RUN yes | ${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager --licenses && \
    ${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager "platforms;android-34" "build-tools;34.0.0"

WORKDIR /app
COPY . .

# Gradle generará el google-services.json automáticamente gracias a settings.gradle.kts
RUN chmod +x ./gradlew
RUN ./gradlew :app:assembleDebug

# --- ETAPA 2: Imagen Final (Emulador) ---
FROM budtmo/docker-android:emulator_11.0

LABEL maintainer="adobe-ajo-team"
LABEL description="AJO Android Template with Emulator"

# Variables de entorno para asegurar que el web VNC esté activo y el emulador inicie correctamente
ENV WEB_VNC=true
ENV EMULATOR_DEVICE="Samsung Galaxy S10"

# Copiamos el APK compilado a la carpeta de instalación del emulador
COPY --from=builder /app/app/build/outputs/apk/debug/app-debug.apk /opt/apps_to_install/app-debug.apk

# Exponemos los puertos necesarios para el emulador y noVNC
EXPOSE 6080 5554 5555
