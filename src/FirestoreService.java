package com.app.service;

import com.app.model.Document;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class FirestoreService {

    private static final Firestore db = FirestoreClient.getFirestore();

    // =====================
    // INSERT DATA
    // =====================
    public static void addDocument(Document doc) {

        try {

            Map<String, Object> data = new HashMap<>();
            data.put("title", doc.title);
            data.put("type", doc.type);
            data.put("courseCode", doc.courseCode);
            data.put("timestamp", FieldValue.serverTimestamp());

            db.collection("documents")
                    .add(data)
                    .get(); // wait for completion

            System.out.println("✅ Document added to Firestore!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================
    // GET DATA
    // =====================
    public static List<Document> getDocuments() {

        List<Document> list = new ArrayList<>();

        try {

            ApiFuture<QuerySnapshot> future =
                    db.collection("documents").get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot doc : documents) {

                list.add(new Document(
                        doc.getString("title"),
                        doc.getString("type"),
                        doc.getString("courseCode")
                ));
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return list;
    }
}