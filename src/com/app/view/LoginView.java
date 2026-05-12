package com.app.view;

import com.app.navigation.SceneManager;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginView extends BorderPane {
    public LoginView() {
        this.getStyleClass().add("login-root");
        VBox box = new VBox(15);
        box.getStyleClass().add("login-card");
        Label title = new Label("Login");
        title.getStyleClass().add("login-title");
        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        Button login = new Button("Login");
        login.getStyleClass().add("button");
        login.setOnAction(e -> SceneManager.showDashboard());
        box.getChildren().addAll(title, username, password, login);
        setCenter(box);
    }
}