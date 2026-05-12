import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;

import javafx.stage.Stage;

public class QuizPage {

    public Scene createScene(Stage stage) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));

            Scene scene = new Scene(loader.load(), 900, 600);

            if (getClass().getResource("styleJ.css") != null) {

                scene.getStylesheets().add(getClass().getResource("styleJ.css").toExternalForm());

            }

            return scene;

        } catch (Exception e) {

            e.printStackTrace();

            return new Scene(new javafx.scene.control.Label("Error loading Quiz Page"), 900, 600);

        }

    }

}
 