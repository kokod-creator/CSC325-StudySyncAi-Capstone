package com.app.view;

import com.app.model.Course;
import com.app.model.Document;
import com.app.navigation.SceneManager;
import com.app.service.MockDataService;
import com.app.service.UploadService;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class UploadView extends BorderPane {

    public UploadView() {

        VBox box = new VBox(15);
        box.getStyleClass().add("card");
        box.setMaxWidth(600);
        box.setAlignment(Pos.TOP_CENTER);

        // Title
        Label title = new Label("Upload Document");
        title.getStyleClass().add("section-title");

        // Drop Area
        VBox dropArea = new VBox(5);
        dropArea.getStyleClass().add("drop-area");
        dropArea.setAlignment(Pos.CENTER);

        Label dropText = new Label("📁 Drop file here or click to browse");
        Label fileInfo = new Label("PDF, PPTX, DOCX up to 50MB");
        Label selectedLabel = new Label();

        dropArea.getChildren().addAll(dropText, fileInfo, selectedLabel);

        // Document Title
        TextField docTitle = new TextField();
        docTitle.setPromptText("Document Title");

        // Document Type (declared early so the drop area can prefill it)
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.setItems(
                FXCollections.observableArrayList(
                        "PDF",
                        "Slides",
                        "Notes"
                )
        );
        typeBox.setPromptText("Document Type");

        final File[] selectedHolder = new File[1];

        dropArea.setOnMouseClicked(e -> {
            File chosen = UploadService.chooseFile(SceneManager.getStage());
            if (chosen == null) {
                return;
            }
            selectedHolder[0] = chosen;
            selectedLabel.setText("Selected: " + chosen.getName());

            if (docTitle.getText().trim().isEmpty()) {
                docTitle.setText(chosen.getName());
            }
            String inferred = inferType(chosen.getName());
            if (inferred != null && typeBox.getValue() == null) {
                typeBox.setValue(inferred);
            }
        });

        // Course Selection
        ComboBox<String> courseBox = new ComboBox<>();

        for (Course c : MockDataService.courses) {
            courseBox.getItems().add(c.getCode());
        }

        courseBox.setPromptText("Select a course...");

        // Upload Button
        Button upload = new Button("Upload Document");
        upload.getStyleClass().add("button");

        upload.setOnAction(e -> {

            if (selectedHolder[0] == null
                    || docTitle.getText().trim().isEmpty()
                    || courseBox.getValue() == null
                    || typeBox.getValue() == null) {

                Alert error = new Alert(
                        Alert.AlertType.ERROR,
                        "Please choose a file and fill all fields."
                );

                error.showAndWait();
                return;
            }

            // Save document
            MockDataService.documents.add(
                    new Document(
                            docTitle.getText().trim(),
                            typeBox.getValue(),
                            courseBox.getValue()
                    )
            );

            Alert success = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Document uploaded successfully!"
            );

            success.showAndWait();

            // Navigate to dashboard
            SceneManager.showDashboard();
        });

        // Back Button
        Button back = new Button("Back");
        back.getStyleClass().add("button-secondary");

        back.setOnAction(e -> SceneManager.showDashboard());

        // Add All Components
        box.getChildren().addAll(
                title,
                dropArea,
                docTitle,
                courseBox,
                typeBox,
                upload,
                back
        );

        setCenter(box);
    }

    private static String inferType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".pdf")) return "PDF";
        if (lower.endsWith(".ppt") || lower.endsWith(".pptx")) return "Slides";
        if (lower.endsWith(".doc") || lower.endsWith(".docx") || lower.endsWith(".txt")) return "Notes";
        return null;
    }
}