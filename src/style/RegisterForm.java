package style;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterForm {

    public void show() {

        Stage stage = new Stage();

        TextField email = new TextField();
        email.setPromptText("Email");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button register = new Button("Register");

        register.setOnAction(e -> {
            System.out.println("Register clicked");
        });

        VBox root = new VBox(10, email, password, register);
        root.setStyle("-fx-padding:20");

        Scene scene = new Scene(root,300,200);

        stage.setScene(scene);
        stage.setTitle("Register");
        stage.show();
    }
}