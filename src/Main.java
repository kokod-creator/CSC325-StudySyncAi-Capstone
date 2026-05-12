
import javafx.application.Application;
import javafx.stage.Stage;
import com.app.service.FirebaseService;
import com.app.navigation.SceneManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        FirebaseService.init();
        SceneManager.init(primaryStage);

        LoginPage loginPage = new LoginPage();
        primaryStage.setTitle("StudySync AI");
        primaryStage.setScene(loginPage.createScene(primaryStage));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}