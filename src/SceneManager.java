package com.app.navigation;

import com.app.view.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static Stage stage;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("StudySpace");
    }

    private static void applyBasicStyle(Scene scene) {
        // Add a basic inline stylesheet to all scenes
        scene.getStylesheets().add(getInlineStyleSheet());
    }

    private static String getInlineStyleSheet() {
        // Return CSS as a string – this is the same as your style.css but embedded
        return "data:text/css," +
                ".root { -fx-font-family: 'Segoe UI', 'System', sans-serif; -fx-font-size: 13px; } " +
                ".login-root { -fx-background-color: #2c3e50; } " +
                ".login-card { -fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5); -fx-padding: 30; -fx-spacing: 15; -fx-alignment: center; -fx-max-width: 350; } " +
                ".login-title { -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; } " +
                ".button { -fx-background-color: #1abc9c; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-weight: bold; } " +
                ".button:hover { -fx-background-color: #16a085; } " +
                ".button-secondary { -fx-background-color: #95a5a6; } " +
                ".button-secondary:hover { -fx-background-color: #7f8c8d; } " +
                ".sidebar { -fx-background-color: #2c3e50; -fx-padding: 20 15; -fx-spacing: 15; } " +
                ".sidebar-button { -fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: center-left; -fx-padding: 10 15; -fx-cursor: hand; } " +
                ".sidebar-button:hover { -fx-background-color: #34495e; } " +
                ".sidebar-button-active { -fx-background-color: #1abc9c; } " +
                ".main-content { -fx-background-color: #f5f7fa; -fx-padding: 20; } " +
                ".card { -fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2); -fx-padding: 20; -fx-spacing: 15; } " +
                ".section-title { -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; } " +
                ".text-field, .combo-box, .password-field { -fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 4; -fx-padding: 8; } " +
                ".text-field:focused, .combo-box:focused { -fx-border-color: #1abc9c; } " +
                ".drop-area { -fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-border-style: dashed; -fx-background-color: #ecf0f1; -fx-padding: 20; -fx-alignment: center; -fx-cursor: hand; } " +
                ".drop-area:hover { -fx-background-color: #d5f5e3; -fx-border-color: #1abc9c; }";
    }

    public static void showLogin() {
        LoginView loginView = new LoginView();
        Scene scene = new Scene(loginView, 900, 600);
        applyBasicStyle(scene);
        stage.setScene(scene);
        stage.show();
    }

    public static void showDashboard() {
        DashboardView view = new DashboardView();
        Scene scene = new Scene(view, 900, 600);
        applyBasicStyle(scene);
        stage.setScene(scene);
    }

    public static void showUpload() {
        UploadView view = new UploadView();
        Scene scene = new Scene(view, 900, 600);
        applyBasicStyle(scene);
        stage.setScene(scene);
    }

    public static void showCourses() {
        CoursesView view = new CoursesView();
        Scene scene = new Scene(view, 900, 600);
        applyBasicStyle(scene);
        stage.setScene(scene);
    }

    public static void showDocuments() {
        DocumentsView view = new DocumentsView();
        Scene scene = new Scene(view, 900, 600);
        applyBasicStyle(scene);
        stage.setScene(scene);
    }
}