package style;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static class Person {
        private final String name;
        private final String email;

        public Person(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }

    @Override
    public void start(Stage stage) {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open");
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(openItem, exitItem);

        Menu editMenu = new Menu("Edit");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        editMenu.getItems().addAll(copyItem, pasteItem);

        menuBar.getMenus().addAll(fileMenu, editMenu);

        // Extra credit: menu actions
        exitItem.setOnAction(e -> stage.close());

        openItem.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Open");
            alert.setHeaderText(null);
            alert.setContentText("Open menu clicked!");
            alert.showAndWait();
        });

        TableView<Person> table = new TableView<>();

        TableColumn<Person, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Person, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail()));

        table.getColumns().addAll(nameCol, emailCol);

        ObservableList<Person> data = FXCollections.observableArrayList(
                new Person("Alice Johnson", "alice@email.com"),
                new Person("Bob Smith", "bob@email.com"),
                new Person("Charlie Brown", "charlie@email.com")
        );

        table.setItems(data);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(table);

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());

        exitItem.setOnAction(e -> stage.close());

        stage.setTitle("JavaFX TableView Assignment");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}