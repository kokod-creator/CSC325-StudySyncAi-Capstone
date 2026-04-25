import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;

public class Controller {

    @FXML
    private StackPane contentArea;

    public void initialize() {
        showNotes(); // default page
    }

    @FXML
    private void showNotes() {
        loadPage("Notes.fxml");
    }

    @FXML
    private void showQuizzes() {
        loadPage("Quizzes.fxml");
    }

    private void loadPage(String path) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource(path));
            contentArea.getChildren().setAll(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
