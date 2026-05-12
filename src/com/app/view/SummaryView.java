package com.app.view;

import com.app.model.GeneratedStudyMaterial;
import com.app.navigation.SceneManager;
import com.app.service.MockDataService;
import com.app.service.OfflineModeService;
import com.app.service.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.SummaryManager;

import java.util.List;

public class SummaryView extends BorderPane {

    public SummaryView() {

        VBox box = new VBox(15);
        box.getStyleClass().add("card");
        box.setMaxWidth(750);
        box.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Your Study Guides");
        title.getStyleClass().add("section-title");

        Label hint = new Label(
                "Pick a recent study guide, or upload a new document to generate one."
        );

        List<GeneratedStudyMaterial> recent = MockDataService.getRecentStudyMaterials(10);
        ObservableList<GeneratedStudyMaterial> items = FXCollections.observableArrayList(recent);

        ListView<GeneratedStudyMaterial> list = new ListView<>(items);
        list.setPrefHeight(160);
        list.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(GeneratedStudyMaterial item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.documentTitle + "  —  " + item.courseCode);
                }
            }
        });

        TextArea summaryOutput = new TextArea();
        summaryOutput.setPromptText(items.isEmpty()
                ? "No study guides yet. Upload a document to generate your first one."
                : "Select a study guide to view it.");
        summaryOutput.setEditable(false);
        summaryOutput.setWrapText(true);
        summaryOutput.setPrefRowCount(14);

        Label status = new Label();

        Button regenerate = new Button("Regenerate");
        regenerate.getStyleClass().add("button-secondary");
        regenerate.setDisable(true);

        final GeneratedStudyMaterial[] selectedRef = new GeneratedStudyMaterial[1];

        list.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            selectedRef[0] = newV;
            if (newV == null) {
                summaryOutput.clear();
                regenerate.setDisable(true);
                status.setText("");
            } else {
                summaryOutput.setText(newV.summaryText);
                regenerate.setDisable(false);
                status.setText("");
            }
        });

        regenerate.setOnAction(e -> {
            GeneratedStudyMaterial mat = selectedRef[0];
            if (mat == null) return;
            if (!OfflineModeService.isOnline()) {
                new Alert(Alert.AlertType.ERROR,
                        "You're offline. You can view this study guide, but regenerating needs internet.")
                        .showAndWait();
                return;
            }
            regenerate.setDisable(true);
            status.setText("Regenerating study guide with AI...");
            SummaryManager summaryManager = new SummaryManager(UserSession.getUserId());
            String topicId = mat.courseCode + ":" + mat.documentTitle;
            String topicText = "Course: " + mat.courseCode + "\nDocument: " + mat.documentTitle
                    + "\n\nPrevious study guide:\n" + mat.summaryText;
            summaryManager.regenerateSummary(topicId, topicText, new SummaryManager.SummaryCallback() {
                @Override
                public void onSuccess(String improved) {
                    // Replace the entry in the recent list so navigation reflects the new guide.
                    int idx = MockDataService.recentStudyMaterials.indexOf(mat);
                    GeneratedStudyMaterial updated = new GeneratedStudyMaterial(
                            mat.documentTitle, mat.courseCode, improved, mat.quizText, mat.questions);
                    updated.shuffleCount = mat.shuffleCount;
                    if (idx >= 0) {
                        MockDataService.recentStudyMaterials.set(idx, updated);
                    }
                    int listIdx = items.indexOf(mat);
                    if (listIdx >= 0) {
                        items.set(listIdx, updated);
                        list.getSelectionModel().select(updated);
                    }
                    summaryOutput.setText(improved);
                    regenerate.setDisable(false);
                    status.setText("Study guide regenerated.");
                }

                @Override
                public void onQuotaExceeded(int usedCount, int maxCount) {
                    regenerate.setDisable(false);
                    new Alert(Alert.AlertType.WARNING,
                            "Study guide limit reached (" + usedCount + " / " + maxCount + ") for this 48-hour window.")
                            .showAndWait();
                    status.setText("");
                }

                @Override
                public void onError(Exception ex) {
                    regenerate.setDisable(false);
                    status.setText("");
                    new Alert(Alert.AlertType.ERROR,
                            "Regeneration failed.\n\n" + ex.getMessage()).showAndWait();
                }
            });
        });

        Button uploadNew = new Button("Upload a new document");
        uploadNew.getStyleClass().add("button");
        uploadNew.setOnAction(e -> SceneManager.showUpload());

        Button back = new Button("Back");
        back.getStyleClass().add("button-secondary");
        back.setOnAction(e -> SceneManager.showDashboard());

        HBox buttons = new HBox(10, uploadNew, regenerate, back);
        buttons.setAlignment(Pos.CENTER);

        box.getChildren().addAll(title, hint, list, buttons, status, summaryOutput);

        setCenter(box);
    }
}
