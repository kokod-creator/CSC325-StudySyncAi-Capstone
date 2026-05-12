package com.app.view;

import com.app.navigation.SceneManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.SummaryManager;

public class SummaryView extends BorderPane {

    private static final String USER_ID = "demo-user";

    private final SummaryManager summaryManager = new SummaryManager(USER_ID);

    public SummaryView() {

        VBox box = new VBox(15);
        box.getStyleClass().add("card");
        box.setMaxWidth(700);
        box.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Notes Summary");
        title.getStyleClass().add("section-title");

        TextField topicIdField = new TextField();
        topicIdField.setPromptText("Note title (used as id, e.g. 'lecture-7')");

        TextArea notes = new TextArea();
        notes.setPromptText("Paste your notes here...");
        notes.setWrapText(true);
        notes.setPrefRowCount(8);

        TextArea summaryOutput = new TextArea();
        summaryOutput.setPromptText("Summary will appear here.");
        summaryOutput.setEditable(false);
        summaryOutput.setWrapText(true);
        summaryOutput.setPrefRowCount(6);

        Label status = new Label();
        Label remaining = new Label("Improvements left: " + SummaryManager.MAX_SUMMARIES_PER_NOTE);

        Button generate = new Button("Generate Summary");
        generate.getStyleClass().add("button");

        Button improve = new Button("Improve Summary");
        improve.getStyleClass().add("button-secondary");
        improve.setDisable(true);

        SummaryManager.SummaryCallback summaryCallback = new SummaryManager.SummaryCallback() {
            @Override
            public void onSuccess(String summaryText) {
                summaryOutput.setText(summaryText);
                String topicId = topicIdField.getText().trim();
                int left = topicId.isEmpty()
                        ? SummaryManager.MAX_SUMMARIES_PER_NOTE
                        : summaryManager.getSummariesRemaining(topicId);
                remaining.setText("Improvements left: " + left);
                improve.setDisable(left <= 0);
                status.setText("Summary ready.");
            }

            @Override
            public void onQuotaExceeded(int usedCount, int maxCount) {
                status.setText("Limit reached (" + usedCount + " / " + maxCount + ").");
                improve.setDisable(true);
            }

            @Override
            public void onError(Exception e) {
                status.setText("Summary failed: " + e.getMessage());
            }
        };

        generate.setOnAction(e -> {
            String topicId = topicIdField.getText().trim();
            String topicText = notes.getText().trim();
            if (topicId.isEmpty() || topicText.isEmpty()) {
                status.setText("Enter both a note title and notes text.");
                return;
            }
            status.setText("Generating summary...");
            summaryManager.generateSummary(topicId, topicText, summaryCallback);
        });

        improve.setOnAction(e -> {
            String topicId = topicIdField.getText().trim();
            String topicText = notes.getText().trim();
            if (topicId.isEmpty() || topicText.isEmpty()) {
                status.setText("Enter both a note title and notes text.");
                return;
            }
            status.setText("Improving summary...");
            summaryManager.regenerateSummary(topicId, topicText, summaryCallback);
        });

        HBox buttons = new HBox(10, generate, improve);
        buttons.setAlignment(Pos.CENTER);

        Button back = new Button("Back");
        back.getStyleClass().add("button-secondary");
        back.setOnAction(e -> SceneManager.showDashboard());

        box.getChildren().addAll(title, topicIdField, notes, buttons, remaining, status, summaryOutput, back);

        setCenter(box);
    }
}
