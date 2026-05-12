package com.app.view;

import com.app.model.Document;
import com.app.navigation.SceneManager;
import com.app.service.MockDataService;

import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DashboardView extends BorderPane {

    public DashboardView() {

        // ---- SIDEBAR ----
        VBox sidebar = new VBox(15);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);

        Label appTitle = new Label("StudySpace");
        appTitle.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;"
        );

        Button uploadBtn = new Button("📤 Upload");
        Button coursesBtn = new Button("📚 Courses");
        Button documentsBtn = new Button("📄 Documents");

        Button[] buttons = {
                uploadBtn,
                coursesBtn,
                documentsBtn
        };

        for (Button btn : buttons) {
            btn.getStyleClass().add("sidebar-button");
            btn.setMaxWidth(Double.MAX_VALUE);
        }

        uploadBtn.setOnAction(e -> SceneManager.showUpload());
        coursesBtn.setOnAction(e -> SceneManager.showCourses());
        documentsBtn.setOnAction(e -> SceneManager.showDocuments());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button signOut = new Button("🚪 Sign out");
        signOut.getStyleClass().add("sidebar-button");

        signOut.setOnAction(e -> {
            System.exit(0);
        });

        sidebar.getChildren().addAll(
                appTitle,
                uploadBtn,
                coursesBtn,
                documentsBtn,
                spacer,
                signOut
        );

        setLeft(sidebar);

        // ---- MAIN CONTENT ----
        VBox main = new VBox(20);
        main.getStyleClass().add("main-content");

        Label welcome = new Label("Dashboard");
        welcome.getStyleClass().add("section-title");

        Label sub = new Label(
                "Welcome back, Asad. Here’s your study overview."
        );

        // ---- STATS CARD ----
        VBox statsCard = new VBox(10);
        statsCard.getStyleClass().add("card");

        Label statsLabel = new Label(
                "Courses: " + MockDataService.courses.size()
                        + " | Documents: "
                        + MockDataService.documents.size()
        );

        statsCard.getChildren().add(statsLabel);

        // ---- RECENT DOCUMENTS ----
        Label recentTitle = new Label("Recent Documents");
        recentTitle.getStyleClass().add("section-title");

        ListView<String> recentList = new ListView<>();
        recentList.setPrefHeight(150);

        for (Document d : MockDataService.getRecentDocuments(5)) {

            recentList.getItems().add(
                    d.title + " - "
                            + d.courseCode
                            + " (" + d.type + ")"
            );
        }

        // ---- ADD ALL TO MAIN ----
        main.getChildren().addAll(
                welcome,
                sub,
                statsCard,
                recentTitle,
                recentList
        );

        setCenter(main);
    }
}