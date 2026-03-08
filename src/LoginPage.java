import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPage {

    private final AuthService authService = new AuthService();

    public Scene createScene(Stage stage) {
        Label title = new Label("StudySync AI");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitle = new Label("Login to your account");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        RadioButton studentRadio = new RadioButton("Student");
        RadioButton guestRadio = new RadioButton("Guest");

        ToggleGroup accountTypeGroup = new ToggleGroup();
        studentRadio.setToggleGroup(accountTypeGroup);
        guestRadio.setToggleGroup(accountTypeGroup);
        studentRadio.setSelected(true);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label guestInfoLabel = new Label("Guest mode will not save data after exit.");
        guestInfoLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");
        guestInfoLabel.setVisible(false);

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(220);

        Button goToRegisterButton = new Button("Create Account");
        goToRegisterButton.setPrefWidth(220);

        studentRadio.setOnAction(e -> {
            emailField.setDisable(false);
            passwordField.setDisable(false);
            guestInfoLabel.setVisible(false);
        });

        guestRadio.setOnAction(e -> {
            emailField.setDisable(true);
            passwordField.setDisable(true);
            emailField.clear();
            passwordField.clear();
            guestInfoLabel.setVisible(true);
        });

        loginButton.setOnAction(e -> {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("");

            if (guestRadio.isSelected()) {
                SessionManager.startGuestSession();
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Guest login successful.");
                System.out.println("Logged in as Guest");
                // stage.setScene(new DashboardPage().createScene(stage));
                return;
            }

            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please enter email and password.");
                return;
            }

            AuthService.AuthResult result = authService.loginStudent(email, password);

            if (result.isSuccess()) {
                SessionManager.startStudentSession(result.getEmail(), result.getIdToken());
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Student login successful.");
                System.out.println("Logged in as student: " + result.getEmail());
                // stage.setScene(new DashboardPage().createScene(stage));
            } else {
                messageLabel.setText(result.getMessage());
            }
        });

        goToRegisterButton.setOnAction(e -> {
            RegisterPage registerPage = new RegisterPage();
            stage.setScene(registerPage.createScene(stage));
        });

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(
                title,
                subtitle,
                studentRadio,
                guestRadio,
                emailField,
                passwordField,
                guestInfoLabel,
                loginButton,
                goToRegisterButton,
                messageLabel
        );

        return new Scene(layout, 400, 520);
    }
}