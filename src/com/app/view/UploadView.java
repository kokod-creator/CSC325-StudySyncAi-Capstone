package com.app.view;

import com.app.model.Course;
import com.app.model.Document;
import com.app.model.GeneratedStudyMaterial;
import com.app.navigation.SceneManager;
import com.app.service.MockDataService;
import com.app.service.OfflineModeService;
import com.app.service.TextExtractor;
import com.app.service.UploadService;
import com.app.service.UserSession;

import java.io.File;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.example.QuizManager;
import org.example.QuotaManager;
import org.example.SummaryManager;

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

        Label status = new Label();

        // Upload Button
        Button upload = new Button("Upload & Generate");
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

            // Offline guard: uploads require AI generation, which needs the network.
            if (!OfflineModeService.isOnline()) {
                new Alert(Alert.AlertType.ERROR,
                        "You're offline. You can view saved documents, study guides, and quizzes, but "
                                + "new uploads need an internet connection to generate AI content.")
                        .showAndWait();
                return;
            }

            String titleText = docTitle.getText().trim();
            String courseCode = courseBox.getValue();
            File file = selectedHolder[0];

            // Per-resource quota: 4 documents per 48h.
            QuotaManager docQuota = new QuotaManager(UserSession.getUserId());
            final boolean[] quotaOk = { false };
            final int[] used = { 0 };
            final int[] max = { 0 };
            docQuota.checkAndIncrement(QuotaManager.Resource.DOCUMENT, new QuotaManager.QuotaCallback() {
                @Override
                public void onResult(boolean allowed, int usedCount, int maxCount) {
                    quotaOk[0] = allowed;
                    used[0] = usedCount;
                    max[0] = maxCount;
                }

                @Override
                public void onError(Exception ex) {
                    quotaOk[0] = false;
                }
            });
            if (!quotaOk[0]) {
                new Alert(Alert.AlertType.WARNING,
                        "Document upload limit reached (" + used[0] + " / " + max[0] + ") for this 48-hour window.")
                        .showAndWait();
                return;
            }

            // Save document
            MockDataService.documents.add(
                    new Document(
                            titleText,
                            typeBox.getValue(),
                            courseCode
                    )
            );

            status.setText("Reading file contents...");
            upload.setDisable(true);

            new Thread(() -> {
                String extracted = TextExtractor.extract(file);
                javafx.application.Platform.runLater(() -> {
                    int len = extracted == null ? 0 : extracted.length();
                    if (extracted == null || len < 200) {
                        upload.setDisable(false);
                        status.setText("");
                        Alert err = new Alert(Alert.AlertType.ERROR,
                                "Could not extract readable text from this file"
                                        + (len > 0 ? " (only " + len + " characters found)" : "")
                                        + ".\n\nThis usually means the document is scanned, image-only, "
                                        + "or password-protected. Try a different file or a text-based PDF.");
                        err.showAndWait();
                        return;
                    }
                    status.setText("Read " + len + " characters. Generating study guide & quiz with AI...");
                    generateStudyMaterial(titleText, courseCode, extracted, status, upload);
                });
            }, "doc-extract").start();
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
                status,
                back
        );

        setCenter(box);
    }

    private static void generateStudyMaterial(String docTitleText,
                                              String courseCode,
                                              String content,
                                              Label status,
                                              Button upload) {
        String userId = UserSession.getUserId();
        String topicId = courseCode + ":" + docTitleText;
        // Pass the actual document content (Tika-extracted) to the AI.
        String topicText = "Course: " + courseCode + "\nDocument: " + docTitleText + "\n\nContent:\n" + content;

        SummaryManager summaryManager = new SummaryManager(userId);
        QuizManager quizManager = new QuizManager(userId);

        summaryManager.generateSummary(topicId, topicText, new SummaryManager.SummaryCallback() {
            @Override
            public void onSuccess(String summaryText) {
                status.setText("Study guide generated. Generating quiz...");
                quizManager.generateQuiz(topicText, new QuizManager.QuizCallback() {
                    @Override
                    public void onSuccess(List<QuizManager.QuizQuestion> questions, int shufflesRemaining) {
                        String quizText = renderQuiz(questions);
                        MockDataService.recentStudyMaterials.add(
                                new GeneratedStudyMaterial(docTitleText, courseCode, summaryText, quizText, questions)
                        );
                        upload.setDisable(false);
                        Alert ok = new Alert(
                                Alert.AlertType.INFORMATION,
                                "Document uploaded. Study guide and quiz generated successfully."
                        );
                        ok.showAndWait();
                        SceneManager.showQuiz();
                    }

                    @Override
                    public void onQuotaExceeded(int usedCount, int maxCount) {
                        MockDataService.recentStudyMaterials.add(
                                new GeneratedStudyMaterial(docTitleText, courseCode, summaryText,
                                        "Quiz not generated: AI quota reached (" + usedCount + " / " + maxCount + ").",
                                        java.util.Collections.emptyList())
                        );
                        upload.setDisable(false);
                        new Alert(Alert.AlertType.WARNING,
                                "AI quota reached for quiz (" + usedCount + " / " + maxCount + "). "
                                        + "Study guide was saved.").showAndWait();
                        SceneManager.showDashboard();
                    }

                    @Override
                    public void onError(Exception ex) {
                        upload.setDisable(false);
                        status.setText("");
                        new Alert(Alert.AlertType.ERROR,
                                "Quiz generation failed.\n\n" + ex.getMessage()).showAndWait();
                    }
                });
            }

            @Override
            public void onQuotaExceeded(int usedCount, int maxCount) {
                upload.setDisable(false);
                new Alert(Alert.AlertType.WARNING,
                        "AI quota reached for study guide (" + usedCount + " / " + maxCount + ").")
                        .showAndWait();
                SceneManager.showDashboard();
            }

            @Override
            public void onError(Exception ex) {
                upload.setDisable(false);
                status.setText("");
                new Alert(Alert.AlertType.ERROR,
                        "Study guide generation failed.\n\n" + ex.getMessage()).showAndWait();
            }
        });
    }

    private static String renderQuiz(List<QuizManager.QuizQuestion> questions) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            QuizManager.QuizQuestion q = questions.get(i);
            sb.append(i + 1).append(". ").append(q.question).append("\n");
            for (String opt : q.options) {
                sb.append("   ").append(opt).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String inferType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".pdf")) return "PDF";
        if (lower.endsWith(".ppt") || lower.endsWith(".pptx")) return "Slides";
        if (lower.endsWith(".doc") || lower.endsWith(".docx") || lower.endsWith(".txt")) return "Notes";
        return null;
    }
}
