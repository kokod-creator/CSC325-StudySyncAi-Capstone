package com.app.view;

import com.app.model.Course;
import com.app.model.Document;
import com.app.navigation.SceneManager;
import com.app.service.MockDataService;

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

        dropArea.getChildren().addAll(dropText, fileInfo);

        dropArea.setOnMouseClicked(e -> {
            Alert info = new Alert(
                    Alert.AlertType.INFORMATION,
                    "File picker would open here."
            );
            info.showAndWait();
        });

        // Document Title
        TextField docTitle = new TextField();
        docTitle.setPromptText("Document Title");

        // Course Selection
        ComboBox<String> courseBox = new ComboBox<>();

        for (Course c : MockDataService.courses) {
            courseBox.getItems().add(c.getCode());
        }

        courseBox.setPromptText("Select a course...");

        // Document Type
        ComboBox<String> typeBox = new ComboBox<>();

        typeBox.setItems(
                FXCollections.observableArrayList(
                        "PDF",
                        "Slides",
                        "Notes"
                )
        );

        typeBox.setPromptText("Document Type");

        // Upload Button
        Button upload = new Button("Upload Document");
        upload.getStyleClass().add("button");

        upload.setOnAction(e -> {

            if (docTitle.getText().trim().isEmpty()
                    || courseBox.getValue() == null
                    || typeBox.getValue() == null) {

                Alert error = new Alert(
                        Alert.AlertType.ERROR,
                        "Please fill all fields."
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
}