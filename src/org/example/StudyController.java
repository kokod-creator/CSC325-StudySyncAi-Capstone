package org.example;

import java.util.List;

public class StudyController {

    private final SummaryManager summaryManager;
    private final QuizManager quizManager;
    private final QuotaManager quotaManager;

    private String summaryText = "";
    private String quizText = "";
    private String statusText = "";
    private String quotaText = "";
    private boolean shuffleVisible;

    public StudyController(String userId) {
        this.summaryManager = new SummaryManager(userId);
        this.quizManager = new QuizManager(userId);
        this.quotaManager = new QuotaManager(userId);
        refreshQuotaDisplay();
    }

    public void loadOrCreateSummary(String topicId, String topicText) {
        setStatus("Loading summary...");
        summaryManager.loadSummary(topicId, new SummaryManager.SummaryLoadCallback() {
            @Override
            public void onLoaded(String loadedSummary, boolean isBadSummary) {
                if (isBadSummary) {
                    generateSummary(topicId, topicText);
                    return;
                }

                summaryText = loadedSummary;
                setStatus("Summary loaded.");
            }

            @Override
            public void onError(Exception e) {
                summaryText = "Could not load summary.";
                setStatus("Error loading summary: " + e.getMessage());
            }
        });
    }

    public void generateSummary(String topicId, String topicText) {
        setStatus("Generating summary...");
        summaryManager.generateSummary(topicId, topicText, new SummaryManager.SummaryCallback() {
            @Override
            public void onSuccess(String generatedSummary) {
                summaryText = generatedSummary;
                setStatus("Summary generated.");
                refreshQuotaDisplay();
            }

            @Override
            public void onQuotaExceeded(int usedCount, int maxCount) {
                setStatus("Summary limit reached (" + usedCount + " / " + maxCount + ").");
            }

            @Override
            public void onError(Exception e) {
                setStatus("Summary failed: " + e.getMessage());
            }
        });
    }

    public void regenerateSummary(String topicId, String topicText) {
        setStatus("Improving summary...");
        summaryManager.regenerateSummary(topicId, topicText, new SummaryManager.SummaryCallback() {
            @Override
            public void onSuccess(String improvedSummary) {
                summaryText = improvedSummary;
                int remaining = summaryManager.getSummariesRemaining(topicId);
                setStatus("Summary improved. " + remaining + " improvement(s) left for this note.");
                refreshQuotaDisplay();
            }

            @Override
            public void onQuotaExceeded(int usedCount, int maxCount) {
                setStatus("No more summary improvements for this note (" + usedCount + " / " + maxCount + ").");
            }

            @Override
            public void onError(Exception e) {
                setStatus("Summary improvement failed: " + e.getMessage());
            }
        });
    }

    public void generateQuiz(String topicText) {
        setStatus("Generating quiz...");
        quizManager.generateQuiz(topicText, new QuizManager.QuizCallback() {
            @Override
            public void onSuccess(List<QuizManager.QuizQuestion> questions, int shufflesRemaining) {
                displayQuiz(questions);
                updateShuffleButton(shufflesRemaining);
                setStatus("Quiz generated. " + shufflesRemaining + " shuffle(s) available.");
                refreshQuotaDisplay();
            }

            @Override
            public void onQuotaExceeded(int usedCount, int maxCount) {
                setStatus("AI quota reached (" + usedCount + " / " + maxCount + ").");
            }

            @Override
            public void onError(Exception e) {
                setStatus("Quiz failed: " + e.getMessage());
            }
        });
    }

    public void shuffleQuiz() {
        quizManager.shuffleQuiz(new QuizManager.ShuffleCallback() {
            @Override
            public void onShuffled(List<QuizManager.QuizQuestion> shuffledQuestions, int shufflesRemaining) {
                displayQuiz(shuffledQuestions);
                updateShuffleButton(shufflesRemaining);
                String message = shufflesRemaining == 0
                        ? "Quiz shuffled. No more shuffles available."
                        : "Quiz shuffled. " + shufflesRemaining + " shuffle(s) left.";
                setStatus(message);
            }

            @Override
            public void onMaxShufflesReached() {
                updateShuffleButton(0);
                setStatus("Maximum shuffles reached (3).");
            }
        });
    }

    public String getSummaryText() {
        return summaryText;
    }

    public String getQuizText() {
        return quizText;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getQuotaText() {
        return quotaText;
    }

    public boolean isShuffleVisible() {
        return shuffleVisible;
    }

    private void displayQuiz(List<QuizManager.QuizQuestion> questions) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            QuizManager.QuizQuestion q = questions.get(i);
            sb.append(i + 1).append(". ").append(q.question).append("\n");
            for (String opt : q.options) {
                sb.append("   ").append(opt).append("\n");
            }
            sb.append("\n");
        }
        quizText = sb.toString();
    }

    private void updateShuffleButton(int shufflesRemaining) {
        shuffleVisible = shufflesRemaining > 0;
    }

    private void refreshQuotaDisplay() {
        quotaManager.getQuotaStatus(QuotaManager.Resource.QUIZ, new QuotaManager.QuotaCallback() {
            @Override
            public void onResult(boolean allowed, int usedCount, int maxCount) {
                quotaText = "Quizzes: " + usedCount + " / " + maxCount;
            }

            @Override
            public void onError(Exception e) {
                quotaText = "Uses unavailable";
            }
        });
    }

    private void setStatus(String message) {
        statusText = message;
    }
}
