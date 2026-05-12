import java.time.LocalDateTime;

public class QuotaTrackingService {

    private static final int DOCUMENT_LIMIT = 4;
    private static final int SUMMARY_LIMIT = 4;
    private static final int QUIZ_LIMIT = 4;
    private static final int QUIZ_SHUFFLE_LIMIT = 3;

    private static int documentsUsed = 0;
    private static int summariesUsed = 0;
    private static int quizzesUsed = 0;
    private static int quizShufflesUsed = 0;

    private static LocalDateTime windowStart = LocalDateTime.now();
    private static LocalDateTime windowEnd = windowStart.plusHours(48);

    private static void resetIfExpired() {
        if (LocalDateTime.now().isAfter(windowEnd)) {
            documentsUsed = 0;
            summariesUsed = 0;
            quizzesUsed = 0;
            quizShufflesUsed = 0;

            windowStart = LocalDateTime.now();
            windowEnd = windowStart.plusHours(48);

            System.out.println("Quota window reset.");
        }
    }

    public static boolean canUploadDocument() {
        resetIfExpired();
        return documentsUsed < DOCUMENT_LIMIT;
    }

    public static boolean canGenerateSummary() {
        resetIfExpired();
        return summariesUsed < SUMMARY_LIMIT;
    }

    public static boolean canGenerateQuiz() {
        resetIfExpired();
        return quizzesUsed < QUIZ_LIMIT;
    }

    public static boolean canShuffleQuiz() {
        resetIfExpired();
        return quizShufflesUsed < QUIZ_SHUFFLE_LIMIT;
    }

    public static void recordDocumentUpload() {
        resetIfExpired();
        documentsUsed++;
    }

    public static void recordSummaryGeneration() {
        resetIfExpired();
        summariesUsed++;
    }

    public static void recordQuizGeneration() {
        resetIfExpired();
        quizzesUsed++;
    }

    public static void recordQuizShuffle() {
        resetIfExpired();
        quizShufflesUsed++;
    }

    public static String getQuotaStatus() {
        resetIfExpired();

        return "Documents: " + documentsUsed + "/" + DOCUMENT_LIMIT +
                "\nSummaries: " + summariesUsed + "/" + SUMMARY_LIMIT +
                "\nQuizzes: " + quizzesUsed + "/" + QUIZ_LIMIT +
                "\nQuiz Shuffles: " + quizShufflesUsed + "/" + QUIZ_SHUFFLE_LIMIT +
                "\nQuota resets at: " + windowEnd;
    }
}