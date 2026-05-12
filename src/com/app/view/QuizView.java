package com.app.view;

import com.app.model.GeneratedStudyMaterial;
import com.app.navigation.SceneManager;
import com.app.service.MockDataService;
import com.app.service.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.QuizManager;

import java.util.List;

public class QuizView extends BorderPane {

    private final QuizManager quizManager = new QuizManager(UserSession.getUserId());

    public QuizView() {

        VBox box = new VBox(15);
        box.getStyleClass().add("card");
        box.setMaxWidth(750);
        box.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Your Quizzes");
        title.getStyleClass().add("section-title");

        Label hint = new Label(
                "Pick a recent quiz, or upload a new document to generate one."
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

        TextArea quizOutput = new TextArea();
        quizOutput.setPromptText(items.isEmpty()
                ? "No quizzes yet. Upload a document to generate your first one."
                : "Select a quiz to view it.");
        quizOutput.setEditable(false);
        quizOutput.setWrapText(true);
        quizOutput.setPrefRowCount(12);

        Label status = new Label();
        Label shufflesLabel = new Label("Shuffles available: " + QuizManager.MAX_SHUFFLES);

        Button shuffle = new Button("Shuffle Questions");
        shuffle.getStyleClass().add("button-secondary");
        shuffle.setDisable(true);

        final GeneratedStudyMaterial[] selectedRef = new GeneratedStudyMaterial[1];

        list.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            selectedRef[0] = newV;
            if (newV == null) {
                quizOutput.clear();
                shuffle.setDisable(true);
                return;
            }
            quizOutput.setText(newV.quizText);
            // Load saved questions and the quiz's persisted shuffle count.
            quizManager.loadQuestions(newV.questions, newV.shuffleCount);
            int shufflesRemaining = quizManager.getShufflesRemaining();
            shufflesLabel.setText("Shuffles available: " + shufflesRemaining + " / " + QuizManager.MAX_SHUFFLES);
            shuffle.setDisable(!quizManager.canShuffle());
            status.setText("Loaded quiz for " + newV.documentTitle + ".");
        });

        shuffle.setOnAction(e -> quizManager.shuffleQuiz(new QuizManager.ShuffleCallback() {
            @Override
            public void onShuffled(List<QuizManager.QuizQuestion> shuffledQuestions, int shufflesRemaining) {
                String rendered = renderQuiz(shuffledQuestions);
                quizOutput.setText(rendered);
                shufflesLabel.setText("Shuffles available: " + shufflesRemaining + " / " + QuizManager.MAX_SHUFFLES);
                shuffle.setDisable(shufflesRemaining <= 0);
                status.setText(shufflesRemaining == 0
                        ? "Shuffled. No more shuffles available."
                        : "Shuffled. " + shufflesRemaining + " left.");

                // Persist the new order and shuffle count back to the saved quiz.
                GeneratedStudyMaterial mat = selectedRef[0];
                if (mat != null) {
                    mat.questions.clear();
                    mat.questions.addAll(shuffledQuestions);
                    mat.quizText = rendered;
                    mat.shuffleCount = quizManager.getShuffleCount();
                }
            }

            @Override
            public void onMaxShufflesReached() {
                shuffle.setDisable(true);
                status.setText("Maximum shuffles reached (" + QuizManager.MAX_SHUFFLES + ").");
            }
        }));

        Button uploadNew = new Button("Upload a new document");
        uploadNew.getStyleClass().add("button");
        uploadNew.setOnAction(e -> SceneManager.showUpload());

        Button back = new Button("Back");
        back.getStyleClass().add("button-secondary");
        back.setOnAction(e -> SceneManager.showDashboard());

        HBox buttons = new HBox(10, uploadNew, shuffle, back);
        buttons.setAlignment(Pos.CENTER);

        box.getChildren().addAll(title, hint, list, buttons, shufflesLabel, status, quizOutput);

        setCenter(box);
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
}
