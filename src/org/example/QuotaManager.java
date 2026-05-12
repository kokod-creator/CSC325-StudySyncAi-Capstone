package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuotaManager {

    public static final int MAX_REQUESTS_PER_WINDOW = 10;
    private static final long WINDOW_DURATION_MS = 48L * 60 * 60 * 1000;


    private static final Map<String, QuotaState> QUOTAS = new ConcurrentHashMap<>();

    private final String userId;

    public QuotaManager(String userId) {

        this.userId = userId;
    }

    public interface QuotaCallback {
        void onResult(boolean allowed, int usedCount, int maxCount);
        void onError(Exception e);
    }

    public interface TimeCallback {
        void onResult(long millisRemaining);
    }



    public void checkAndIncrement(QuotaCallback callback) {
        try {
            QuotaResult result = updateQuota(true);
            callback.onResult(result.allowed, result.usedCount, MAX_REQUESTS_PER_WINDOW);
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    public void getQuotaStatus(QuotaCallback callback) {
        try {
            QuotaResult result = updateQuota(false);
            callback.onResult(result.allowed, result.usedCount, MAX_REQUESTS_PER_WINDOW);
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    public void getTimeUntilReset(TimeCallback callback) {
        QuotaState state = QUOTAS.get(userId);
        if (state == null) {
            callback.onResult(0);
            return;
        }

        synchronized (state) {
            long elapsed = System.currentTimeMillis() - state.windowStart;
            callback.onResult(Math.max(0, WINDOW_DURATION_MS - elapsed));
        }
    }

    private QuotaResult updateQuota(boolean increment) {
        QuotaState state = QUOTAS.computeIfAbsent(userId, ignored -> new QuotaState());

        synchronized (state) {
            long now = System.currentTimeMillis();
            if (state.windowStart == 0 || now - state.windowStart >= WINDOW_DURATION_MS) {
                state.windowStart = now;
                state.usageCount = 0;
            }

            if (state.usageCount >= MAX_REQUESTS_PER_WINDOW) {
                return new QuotaResult(false, state.usageCount);
            }

            if (increment) {
                state.usageCount++;
            }

            return new QuotaResult(true, state.usageCount);
        }
    }

    private static class QuotaState {
        private long windowStart;
        private int usageCount;
    }

    private record QuotaResult(boolean allowed, int usedCount) {
    }
}
