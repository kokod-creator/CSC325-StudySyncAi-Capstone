import java.net.HttpURLConnection;
import java.net.URL;

public class OfflineModeService {

    public static boolean isOnline() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            int responseCode = connection.getResponseCode();

            return responseCode >= 200 && responseCode < 400;

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean canGenerateAI() {
        return isOnline() && SessionManager.isStudent();
    }

    public static boolean canUploadFiles() {
        return isOnline() && SessionManager.isStudent();
    }

    public static String getOfflineMessage() {
        if (!isOnline()) {
            return "Offline mode is active. You can view saved documents, summaries, and quizzes, but you cannot upload or generate new AI content.";
        }

        if (SessionManager.isGuest()) {
            return "Guest mode is active. Your data will not be saved after you exit.";
        }

        return "Online mode is active.";
    }
}