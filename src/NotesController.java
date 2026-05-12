import com.app.service.UploadService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class NotesController {

    //Notes text area
    @FXML
    private TextArea notesArea;

    //Summary display area
    @FXML
    private TextArea summaryArea;

    //Note title field
    @FXML
    private TextField noteTitleField;


    /**
     * Saves notes locally into saved_notes folder.
     */
    @FXML
    private void saveNotes() {

        try {

            // Create folder if it doesn't exist
            File folder = new File("saved_notes");

            if (!folder.exists()) {
                folder.mkdir();
            }

            String fileName = noteTitleField.getText();

            // Prevent empty filenames
            if (fileName.isEmpty()) {
                System.out.println("Please enter a note title.");
                return;
            }

            FileWriter writer =
                    new FileWriter("saved_notes/" + fileName + ".txt");

            writer.write(notesArea.getText());

            writer.close();

            System.out.println("Notes saved successfully.");

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Loads previously saved notes.
     */
    @FXML
    private void loadNotes() {

        try {

            FileChooser fileChooser = new FileChooser();

            fileChooser.setInitialDirectory(
                    new File("saved_notes"));

            File selectedFile =
                    fileChooser.showOpenDialog(null);

            if (selectedFile != null) {

                String content =
                        Files.readString(selectedFile.toPath());

                notesArea.setText(content);
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    /**
     * Generates a simple summary from notes.
     */
    @FXML
    private void generateSummary() {

        String notes = notesArea.getText();

        if (notes.length() < 200) {

            summaryArea.setText(notes);

        } else {

            summaryArea.setText(
                    notes.substring(0, 200) + "...");
        }
    }

    /**
     * Uploads a file using UploadService.
     */
    @FXML
    private void uploadFile() {

        File selectedFile =
                UploadService.chooseFile(null);

        if (selectedFile != null) {

            System.out.println(
                    "Uploaded: " + selectedFile.getName());

            // TEMPORARY:
            // Later you will extract text from PDFs/docs
        }
    }
}
