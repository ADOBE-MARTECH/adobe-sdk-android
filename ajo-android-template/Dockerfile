# --- ETAPA 1: Compilar el APK ---
# (Esta etapa es idéntica a la anterior y ya sabemos que funciona)
FROM eclipse-temurin:17-jdk as builder

ARG GOOGLE_SERVICES_JSON_CONTENT_ARG
ARG ADOBE_APP_ID_ARG
ARG ADOBE_ASSURANCE_SESSION_ID_ARG

ENV ANDROID_SDK_ROOT /opt/sdk
RUN apt-get update && apt-get install -y --no-install-recommends wget unzip && \
    mkdir -p ${ANDROID_SDK_ROOT} && \
    wget -O sdk-tools.zip 'https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip' && \
    unzip sdk-tools.zip -d ${ANDROID_SDK_ROOT}/cmdline-tools && \
    mv ${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools ${ANDROID_SDK_ROOT}/cmdline-tools/latest && \
    yes | ${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager --licenses && \
    ${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager "platforms;android-34" "build-tools;34.0.0"

COPY . /project
WORKDIR /project

RUN echo "GOOGLE_SERVICES_JSON_CONTENT=${GOOGLE_SERVICES_JSON_CONTENT_ARG}" > .env.local
RUN echo "ADOBE_APP_ID=${ADOBE_APP_ID_ARG}" >> .env.local
RUN echo "ADOBE_ASSURANCE_SESSION_ID=${ADOBE_ASSURANCE_SESSION_ID_ARG}" >> .env.local

# Le damos permisos al script de Gradle y compilamos el APK.
# Nuestro script de Gradle leerá el .env.local que acabamos de crear.
RUN chmod +x ./gradlew
RUN ./gradlew :app:assembleDebug

# --- ETAPA 2: Configurar el Emulador ---
# Usamos la imagen de emulador de Android (API 25) que ya incluye VNC
FROM budtmo/docker-android-x86-7.1.1

# Copiamos nuestro APK compilado a una carpeta mágica que la imagen usa
# para auto-instalar aplicaciones al arrancar.
COPY --from=builder /project/app/build/outputs/apk/debug/app-debug.apk /opt/apps_to_install/

# Exponemos el puerto de VNC (para ver desde el navegador) y los puertos de ADB (opcional, para depurar).
EXPOSE 6080 5554 5555