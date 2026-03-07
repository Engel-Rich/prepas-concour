package com.mutrix.prepa.infrastructure.configs;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseConfiguration {

    @Value("classpath:firebase-service-account.json")
    private Resource firebaseResource;

    private static final Logger logs = LoggerFactory.getLogger(FirebaseConfiguration.class);

    @Bean
    public FirebaseApp initializeFirebase() throws Exception {

        logs.debug("Firebase service initialisation");

        if (!FirebaseApp.getApps().isEmpty()) {
            logs.debug("Firebase service is registred yet");
            return FirebaseApp.getInstance();
        }

        InputStream serviceAccount = firebaseResource.getInputStream();

        // String content = new String(firebaseResource.getInputStream().readAllBytes(),
        // StandardCharsets.UTF_8);
        // logs.info("Firebase service account content: {}", content);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(com.google.auth.oauth2.GoogleCredentials.fromStream(serviceAccount))
                .build();
        return FirebaseApp.initializeApp(options);
    }

}
