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
        quotaManager.checkAndIncrement(new QuotaManager.QuotaCallback() {
            @Override
            public void onResult(boolean allowed, int usedCount, int maxCount) {

                if (!allowed) {
                     callback.onQuotaExceeded(usedCount, maxCount);
                    return;
                }

                 String summaryText = createSummary(topicText, 0);
                   saveSummary(topicId, topicText, summaryText, 0);
                callback.onSuccess(summaryText);
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

        if (currentRegens >= MAX_SUMMARIES_PER_NOTE) {
            callback.onQuotaExceeded(currentRegens, MAX_SUMMARIES_PER_NOTE);
            return;
        }

        quotaManager.checkAndIncrement(new QuotaManager.QuotaCallback() {
            @Override
            public void onResult(boolean allowed, int usedCount, int maxCount) {
                if (!allowed) {
                    callback.onQuotaExceeded(usedCount, maxCount);
                    return;
                }

                int newRegenCount = currentRegens + 1;
                String summaryText = createSummary(topicText, newRegenCount);
                saveSummary(topicId, topicText, summaryText, newRegenCount);
                callback.onSuccess(summaryText);
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
        return Math.max(0, MAX_SUMMARIES_PER_NOTE - used);
    }

    private void saveSummary(String topicId, String topicText, String summaryText, int regenCount) {
        SUMMARIES.put(summaryKey(topicId),
                new SummaryRecord(topicText, summaryText, regenCount, System.currentTimeMillis()));
    }

    private String createSummary(String topicText, int improvementLevel) {
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
