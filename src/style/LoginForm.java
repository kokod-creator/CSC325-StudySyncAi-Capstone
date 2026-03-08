package style;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginForm {

    public void show() {

        Stage stage = new Stage();

        TextField email = new TextField();
        email.setPromptText("Email");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button login = new Button("Sign In");

        login.setOnAction(e -> {
            System.out.println("Login clicked");
        });

        VBox root = new VBox(10, email, password, login);
        root.setStyle("-fx-padding:20");

        Scene scene = new Scene(root,300,200);

        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}