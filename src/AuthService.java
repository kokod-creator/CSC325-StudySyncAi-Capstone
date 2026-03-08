import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {

    private static final String API_KEY = "AIzaSyD8B2mLSfSxEky3EV7vqik1SS_HAdnTGPo";

    private static final String SIGN_UP_URL =
            "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY;

    private static final String SIGN_IN_URL =
            "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;

    private final HttpClient client = HttpClient.newHttpClient();

    public AuthResult registerStudent(String email, String password) {
        String json = """
                {
                  "email": "%s",
                  "password": "%s",
                  "returnSecureToken": true
                }
                """.formatted(email, password);

        return sendAuthRequest(SIGN_UP_URL, json);
    }

    public AuthResult loginStudent(String email, String password) {
        String json = """
                {
                  "email": "%s",
                  "password": "%s",
                  "returnSecureToken": true
                }
                """.formatted(email, password);

        return sendAuthRequest(SIGN_IN_URL, json);
    }

    private AuthResult sendAuthRequest(String url, String jsonBody) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();

            if (response.statusCode() == 200) {
                String email = extractValue(body, "email");
                String idToken = extractValue(body, "idToken");
                return new AuthResult(true, "Success", email, idToken);
            } else {
                String errorMessage = parseFirebaseError(body);
                return new AuthResult(false, errorMessage, null, null);
            }
        } catch (IOException | InterruptedException e) {
            return new AuthResult(false, "Connection error: " + e.getMessage(), null, null);
        }
    }

    private String extractValue(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return json.substring(start, end);
    }

    private String parseFirebaseError(String json) {
        if (json.contains("EMAIL_EXISTS")) return "This email is already registered.";
        if (json.contains("INVALID_PASSWORD")) return "Incorrect password.";
        if (json.contains("EMAIL_NOT_FOUND")) return "No account found for that email.";
        if (json.contains("INVALID_EMAIL")) return "Invalid email address.";
        if (json.contains("WEAK_PASSWORD")) return "Password must be at least 6 characters.";
        if (json.contains("TOO_MANY_ATTEMPTS_TRY_LATER")) return "Too many attempts. Please try again later.";
        return "Authentication failed.";
    }

    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final String email;
        private final String idToken;

        public AuthResult(boolean success, String message, String email, String idToken) {
            this.success = success;
            this.message = message;
            this.email = email;
            this.idToken = idToken;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getEmail() {
            return email;
        }

        public String getIdToken() {
            return idToken;
        }
    }
}