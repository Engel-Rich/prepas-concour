package com.mutrix.prepa.infrastructure.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class FirebaseConfiguration {

    /**
     * JSON complet du compte de service Firebase.
     * En production, injectée via la variable d'environnement FIREBASE_SERVICE_ACCOUNT_JSON.
     * En local, peut aussi pointer vers le fichier classpath en fallback.
     */
    @Value("${firebase.service-account-json:}")
    private String serviceAccountJson;

    /** Fallback : fichier classpath pour le développement local */
    @Value("${firebase.service-account-file:classpath:firebase-service-account.json}")
    private Resource serviceAccountFile;

    @Bean
    public FirebaseApp initializeFirebase() throws Exception {

        if (!FirebaseApp.getApps().isEmpty()) {
            log.debug("Firebase déjà initialisé – réutilisation de l'instance existante");
            return FirebaseApp.getInstance();
        }

        InputStream serviceAccount = resolveCredentials();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(options);
        log.info("Firebase initialisé avec succès (projet : {})", app.getName());
        return app;
    }

    /**
     * Résolution des credentials Firebase :
     * 1. Variable d'environnement FIREBASE_SERVICE_ACCOUNT_JSON (prioritaire – prod / Docker)
     * 2. Fichier classpath firebase-service-account.json (développement local)
     */
    private InputStream resolveCredentials() throws Exception {
        if (StringUtils.hasText(serviceAccountJson)) {
            log.info("Firebase : chargement des credentials depuis la variable d'environnement");
            return new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8));
        }

        if (serviceAccountFile.exists()) {
            log.info("Firebase : chargement des credentials depuis le fichier classpath");
            return serviceAccountFile.getInputStream();
        }

        throw new IllegalStateException(
                "Impossible de trouver les credentials Firebase. " +
                "Définissez la variable d'environnement FIREBASE_SERVICE_ACCOUNT_JSON " +
                "ou placez firebase-service-account.json dans src/main/resources/.");
    }
}
