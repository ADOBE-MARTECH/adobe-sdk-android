# --- ETAPA 1: Compilación del APK ---
FROM eclipse-temurin:17-jdk-jammy AS builder

# Argumentos que recibiremos desde GitHub Actions (Secretos)
ARG GOOGLE_SERVICES_JSON_CONTENT
ARG ADOBE_APP_ID
ARG ADOBE_ASSURANCE_SESSION_ID

ENV ANDROID_SDK_ROOT /opt/android-sdk
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

# Creamos el archivo .env.local dinámicamente para que Gradle lo use durante el build
RUN echo "GOOGLE_SERVICES_JSON_CONTENT=${GOOGLE_SERVICES_JSON_CONTENT}" > .env.local && \
    echo "ADOBE_APP_ID=${ADOBE_APP_ID}" >> .env.local && \
    echo "ADOBE_ASSURANCE_SESSION_ID=${ADOBE_ASSURANCE_SESSION_ID}" >> .env.local

RUN chmod +x ./gradlew
RUN ./gradlew :app:assembleDebug

# --- ETAPA 2: Imagen Final (Ligera) ---
# Si solo quieres distribuir el APK, puedes usar una imagen base de nginx o similar para servirlo,
# o simplemente terminar aquí. Si quieres el emulador, mantenemos la base anterior:
FROM budtmo/docker-android-x86-7.1.1

LABEL maintainer="tu-nombre"
LABEL description="AJO Android Template with Emulator"

# Copiamos el APK compilado en la etapa anterior
COPY --from=builder /app/app/build/outputs/apk/debug/app-debug.apk /opt/apps_to_install/

EXPOSE 6080 5554 5555
