package com.app.view;

import com.app.navigation.SceneManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.QuizManager;

import java.util.List;

public class QuizView extends BorderPane {

    private static final String USER_ID = "demo-user";

    private final QuizManager quizManager = new QuizManager(USER_ID);

    public QuizView() {

        VBox box = new VBox(15);
        box.getStyleClass().add("card");
        box.setMaxWidth(700);
        box.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Quiz Generator");
        title.getStyleClass().add("section-title");

        TextArea topicInput = new TextArea();
        topicInput.setPromptText("Paste notes or a topic to generate quiz questions from...");
        topicInput.setPrefRowCount(5);
        topicInput.setWrapText(true);

        TextArea quizOutput = new TextArea();
        quizOutput.setPromptText("Generated questions will appear here.");
        quizOutput.setEditable(false);
        quizOutput.setWrapText(true);
        quizOutput.setPrefRowCount(12);

        Label status = new Label();
        Label shufflesLabel = new Label("Shuffles available: " + QuizManager.MAX_SHUFFLES);

        Button generate = new Button("Generate Quiz");
        generate.getStyleClass().add("button");

        Button shuffle = new Button("Shuffle Questions");
        shuffle.getStyleClass().add("button-secondary");
        shuffle.setDisable(true);

        generate.setOnAction(e -> {
            String topic = topicInput.getText().trim();
            if (topic.isEmpty()) {
                status.setText("Enter some topic text first.");
                return;
            }
            status.setText("Generating quiz...");
            quizManager.generateQuiz(topic, new QuizManager.QuizCallback() {
                @Override
                public void onSuccess(List<QuizManager.QuizQuestion> questions, int shufflesRemaining) {
                    quizOutput.setText(renderQuiz(questions));
                    shufflesLabel.setText("Shuffles available: " + shufflesRemaining);
                    shuffle.setDisable(shufflesRemaining <= 0);
                    status.setText("Quiz generated.");
                }

                @Override
                public void onQuotaExceeded(int usedCount, int maxCount) {
                    status.setText("Daily AI quota reached (" + usedCount + " / " + maxCount + ").");
                }

                @Override
                public void onError(Exception ex) {
                    status.setText("Quiz failed: " + ex.getMessage());
                }
            });
        });

        shuffle.setOnAction(e -> quizManager.shuffleQuiz(new QuizManager.ShuffleCallback() {
            @Override
            public void onShuffled(List<QuizManager.QuizQuestion> shuffledQuestions, int shufflesRemaining) {
                quizOutput.setText(renderQuiz(shuffledQuestions));
                shufflesLabel.setText("Shuffles available: " + shufflesRemaining);
                shuffle.setDisable(shufflesRemaining <= 0);
                status.setText(shufflesRemaining == 0
                        ? "Shuffled. No more shuffles available."
                        : "Shuffled. " + shufflesRemaining + " left.");
            }

            @Override
            public void onMaxShufflesReached() {
                shuffle.setDisable(true);
                status.setText("Maximum shuffles reached (" + QuizManager.MAX_SHUFFLES + ").");
            }
        }));

        HBox buttons = new HBox(10, generate, shuffle);
        buttons.setAlignment(Pos.CENTER);

        Button back = new Button("Back");
        back.getStyleClass().add("button-secondary");
        back.setOnAction(e -> SceneManager.showDashboard());

        box.getChildren().addAll(title, topicInput, buttons, shufflesLabel, status, quizOutput, back);

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
