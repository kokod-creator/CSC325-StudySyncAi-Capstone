package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SummaryManager {

    public static final int MAX_SUMMARIES_PER_NOTE = 3;
    private static final int MIN_SUMMARY_LENGTH = 50;


    private static final Map<String, SummaryRecord> SUMMARIES = new ConcurrentHashMap<>();

    private final String userId;
    private final QuotaManager quotaManager;

    public SummaryManager(String userId) {
        this.userId = userId;
        this.quotaManager = new QuotaManager(userId);
    }

    public interface SummaryCallback {
        void onSuccess(String summaryText);

        void onQuotaExceeded(int usedCount, int maxCount);
        void onError(Exception e);
    }

    public interface SummaryLoadCallback {
        void onLoaded(String summaryText, boolean isBadSummary);
        void onError(Exception e);
    }



    public void generateSummary(String topicId, String topicText, SummaryCallback callback) {
        quotaManager.checkAndIncrement(QuotaManager.Resource.SUMMARY, new QuotaManager.QuotaCallback() {
            @Override
            public void onResult(boolean allowed, int usedCount, int maxCount) {

                if (!allowed) {
                     callback.onQuotaExceeded(usedCount, maxCount);
                    return;
                }

                new Thread(() -> {
                    try {
                        String summaryText = createSummary(topicText, 0);
                        saveSummary(topicId, topicText, summaryText, 0);
                        javafx.application.Platform.runLater(() -> callback.onSuccess(summaryText));
                    } catch (Exception e) {
                        javafx.application.Platform.runLater(() -> callback.onError(e));
                    }
                }, "ai-summary-gen").start();
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void loadSummary(String topicId, SummaryLoadCallback callback) {
        try {
            SummaryRecord record = SUMMARIES.get(summaryKey(topicId));
            if (record == null) {
                callback.onLoaded(null, true);
                return;
            }

            callback.onLoaded(record.summaryText(), isSummaryBad(record.summaryText()));
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    public void regenerateSummary(String topicId, String topicText, SummaryCallback callback) {
        SummaryRecord existing = SUMMARIES.get(summaryKey(topicId));
        int currentRegens = existing == null ? 0 : existing.regenCount();

        quotaManager.checkAndIncrement(QuotaManager.Resource.SUMMARY, new QuotaManager.QuotaCallback() {
            @Override
            public void onResult(boolean allowed, int usedCount, int maxCount) {
                if (!allowed) {
                    callback.onQuotaExceeded(usedCount, maxCount);
                    return;
                }

                int newRegenCount = currentRegens + 1;
                new Thread(() -> {
                    try {
                        String summaryText = createSummary(topicText, newRegenCount);
                        saveSummary(topicId, topicText, summaryText, newRegenCount);
                        javafx.application.Platform.runLater(() -> callback.onSuccess(summaryText));
                    } catch (Exception e) {
                        javafx.application.Platform.runLater(() -> callback.onError(e));
                    }
                }, "ai-summary-regen").start();
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public boolean isSummaryBad(String summaryText) {
        if (summaryText == null || summaryText.trim().isEmpty()) {
            return true;
        }
        if (summaryText.trim().length() < MIN_SUMMARY_LENGTH) {
            return true;
        }
        return summaryText.toLowerCase().contains("error") && summaryText.trim().length() < 100;
    }

    public int getSummariesRemaining(String topicId) {
        SummaryRecord record = SUMMARIES.get(summaryKey(topicId));
        int used = record == null ? 0 : record.regenCount();
        return Math.max(0, QuotaManager.MAX_PER_WINDOW - used);
    }

    private void saveSummary(String topicId, String topicText, String summaryText, int regenCount) {
        SUMMARIES.put(summaryKey(topicId),
                new SummaryRecord(topicText, summaryText, regenCount, System.currentTimeMillis()));
    }

    private String createSummary(String topicText, int improvementLevel) {
        if (AiService.isConfigured()) {
            String prompt = topicText;
            if (improvementLevel > 0) {
                prompt = "Improved version " + improvementLevel + " — make this clearer, sharper, "
                        + "and easier to remember for an exam, while staying ONLY within the document's content:\n"
                        + topicText;
            }
            String aiSummary = AiService.generateSummary(prompt);
            if (aiSummary == null || aiSummary.isBlank()) {
                throw new RuntimeException("AI study guide generation returned no content. "
                        + "Check your network, GEMINI_API_KEY, and Gemini quota.");
            }
            return aiSummary;
        }

        // No API key configured — produce a minimal placeholder so the app still works offline.
        String cleanText = topicText == null ? "" : topicText.trim();
        String focus = cleanText.length() > 120 ? cleanText.substring(0, 120) + "..." : cleanText;

        if (improvementLevel == 0) {
            return "Summary: " + focus
                    + " Key idea: understand the main facts, important terms, and how they connect for review.";
        }

        return "Improved summary " + improvementLevel + ": " + focus
                + " This version is clearer for studying: it highlights the central concept, explains why it matters, "
                + "and turns the note into a focused review point you can remember before a quiz.";
    }

    private String summaryKey(String topicId) {
        return userId + ":" + topicId;
    }

    private record SummaryRecord(String topicText, String summaryText, int regenCount, long generatedAt) {
    }
}
