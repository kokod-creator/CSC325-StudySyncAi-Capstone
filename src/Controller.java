import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import java.io.*;

public class Controller {

    @FXML
    private StackPane contentArea;

    //Loads notes page when app starts
    public void initialize() {
        showNotes();
    }

    //Displays Notes page
    @FXML
    private void showNotes() {
        loadPage("Notes.fxml");
    }

    //Displays Quizzes page
    @FXML
    private void showQuizzes() {
        loadPage("Quizzes.fxml");
    }

    //Loads selected FXML page into content area
    private void loadPage(String path) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource(path));
            contentArea.getChildren().setAll(page);
        } catch (Exception e) {
            System.out.println("Failed to load page " + path);
            e.printStackTrace();
        }
    }
}
