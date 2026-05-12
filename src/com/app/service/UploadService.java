package com.app.service;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class UploadService {

    private static final long MAX_FILE_SIZE = 80L * 1024 * 1024; // 80MB

    public static File chooseFile(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Document or Slide File");

        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents and Slides",
                        "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = chooser.showOpenDialog(stage);

        if (selectedFile == null) {
            return null;
        }

        if (selectedFile.length() > MAX_FILE_SIZE) {
            System.out.println("File is too large. Maximum size is 80MB.");
            return null;
        }

        System.out.println("Selected file: " + selectedFile.getName());
        return selectedFile;
    }
}
