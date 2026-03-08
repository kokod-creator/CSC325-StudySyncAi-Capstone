package style;

import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreen {

    public static void show(Stage stage, Runnable onFinished) {

        Label label = new Label("Loading Application...");
        label.setStyle("-fx-font-size: 24px;");

        StackPane root = new StackPane(label);

        Scene scene = new Scene(root, 800, 500);

        stage.setScene(scene);
        stage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(2));

        delay.setOnFinished(e -> onFinished.run());

        delay.play();
    }
}