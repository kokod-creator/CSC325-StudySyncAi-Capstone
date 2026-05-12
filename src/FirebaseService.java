package com.app.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.InputStream;

public class FirebaseService {

    public static void init() {

        try {

            InputStream serviceAccount =
                    FirebaseService.class.getClassLoader()
                            .getResourceAsStream("firebase-key.json");

            if (serviceAccount == null) {
                throw new RuntimeException("firebase-key.json not found in resources");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            System.out.println("🔥 Firebase Connected Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}