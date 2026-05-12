package com.app.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

public class AuthService {

    // =========================
    // REGISTER USER
    // =========================
    public static String register(String email, String password) {

        try {

            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password);

            UserRecord user = FirebaseAuth.getInstance().createUser(request);

            return user.getUid();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // =========================
    // LOGIN (handled in frontend validation)
    // Firebase Admin does not handle client login directly
    // =========================
}