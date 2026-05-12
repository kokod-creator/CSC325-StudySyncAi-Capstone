package com.app.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.nio.file.Files;
import java.nio.file.Paths;

public class StorageService {

    private static final String BUCKET = "YOUR_PROJECT_ID.appspot.com";

    public static String uploadFile(String filePath, String fileName) {

        try {

            Storage storage = StorageOptions.getDefaultInstance().getService();

            byte[] bytes = Files.readAllBytes(Paths.get(filePath));

            BlobId blobId = BlobId.of(BUCKET, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

            storage.create(blobInfo, bytes);

            String url = "https://storage.googleapis.com/" + BUCKET + "/" + fileName;

            return url;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}