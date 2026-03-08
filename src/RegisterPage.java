import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterPage {

    private final AuthService authService = new AuthService();

    public Scene createScene(Stage stage) {
        Label title = new Label("Create Account");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitle = new Label("Register for StudySync AI");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        RadioButton studentRadio = new RadioButton("Student");
        RadioButton guestRadio = new RadioButton("Guest");

        ToggleGroup accountTypeGroup = new ToggleGroup();
        studentRadio.setToggleGroup(accountTypeGroup);
        guestRadio.setToggleGroup(accountTypeGroup);
        studentRadio.setSelected(true);

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        Label guestInfoLabel = new Label("Guest mode does not create an account and will not save data after exit.");
        guestInfoLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");
        guestInfoLabel.setVisible(false);

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(220);

        Button backToLoginButton = new Button("Back to Login");
        backToLoginButton.setPrefWidth(220);

        studentRadio.setOnAction(e -> {
            nameField.setDisable(false);
            emailField.setDisable(false);
            passwordField.setDisable(false);
            confirmPasswordField.setDisable(false);
            guestInfoLabel.setVisible(false);
        });

        guestRadio.setOnAction(e -> {
            nameField.setDisable(false);
            emailField.setDisable(true);
            passwordField.setDisable(true);
            confirmPasswordField.setDisable(true);

            emailField.clear();
            passwordField.clear();
            confirmPasswordField.clear();

            guestInfoLabel.setVisible(true);
        });

        registerButton.setOnAction(e -> {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("");

            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                messageLabel.setText("Please enter your name.");
                return;
            }

            if (guestRadio.isSelected()) {
                SessionManager.startGuestSession();
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Guest session started.");
                System.out.println("Guest session started for: " + name);
                // stage.setScene(new DashboardPage().createScene(stage));
                return;
            }

            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                messageLabel.setText("Please fill in all student account fields.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                messageLabel.setText("Passwords do not match.");
                return;
            }

            AuthService.AuthResult result = authService.registerStudent(email, password);

            if (result.isSuccess()) {
                SessionManager.startStudentSession(result.getEmail(), result.getIdToken());
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Registration successful.");
                System.out.println("Registered student: " + result.getEmail());
                // stage.setScene(new DashboardPage().createScene(stage));
            } else {
                messageLabel.setText(result.getMessage());
            }
        });

        backToLoginButton.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            stage.setScene(loginPage.createScene(stage));
        });

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(
                title,
                subtitle,
                studentRadio,
                guestRadio,
                nameField,
                emailField,
                passwordField,
                confirmPasswordField,
                guestInfoLabel,
                registerButton,
                backToLoginButton,
                messageLabel
        );

        return new Scene(layout, 420, 620);
    }
}