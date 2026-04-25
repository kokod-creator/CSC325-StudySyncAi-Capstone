package style;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SplashScreen.show(stage, () -> {
            showMainApp(stage);
        });
    }

    private void showMainApp(Stage stage) {

        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");

        MenuItem loginItem = new MenuItem("Login");
        MenuItem registerItem = new MenuItem("Register");
        MenuItem uploadItem = new MenuItem("Upload Picture");
        MenuItem exitItem = new MenuItem("Exit");

        fileMenu.getItems().addAll(loginItem, registerItem, uploadItem, exitItem);

        Menu editMenu = new Menu("Edit");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");

        editMenu.getItems().addAll(copyItem, pasteItem);

        menuBar.getMenus().addAll(fileMenu, editMenu);

        // Menu actions
        loginItem.setOnAction(e -> new LoginForm().show());
        registerItem.setOnAction(e -> new RegisterForm().show());
        uploadItem.setOnAction(e -> uploadPicture(stage));
        exitItem.setOnAction(e -> stage.close());

        TableView<LogEntry> table = new TableView<>();

        TableColumn<LogEntry, String> messageCol =
                new TableColumn<>("Output");

        messageCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getMessage()));

        table.getColumns().add(messageCol);

        ObservableList<LogEntry> logs = FXCollections.observableArrayList();
        logs.add(new LogEntry("Application started"));

        table.setItems(logs);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(table);

        Scene scene = new Scene(root, 800, 500);

        scene.getStylesheets().add(
                Main.class.getResource("styleJ.css").toExternalForm()
        );

        stage.setTitle("JavaFX Firebase App");
        stage.setScene(scene);
        stage.show();
    }

    private void uploadPicture(Stage stage) {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Profile Picture");

        File file = chooser.showOpenDialog(stage);

        if(file != null){
            System.out.println("Selected file: " + file.getName());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}