public class SessionManager {
    private static boolean guest;
    private static String userEmail;
    private static String idToken;

    public static void startStudentSession(String email, String token) {
        guest = false;
        userEmail = email;
        idToken = token;
    }

    public static void startGuestSession() {
        guest = true;
        userEmail = "Guest";
        idToken = null;
    }

    public static boolean isGuest() {
        return guest;
    }

    public static boolean isStudent() {
        return !guest && userEmail != null;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static String getIdToken() {
        return idToken;
    }

    public static void clearSession() {
        guest = false;
        userEmail = null;
        idToken = null;
    }
}