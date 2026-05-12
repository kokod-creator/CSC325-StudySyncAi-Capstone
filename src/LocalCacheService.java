import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocalCacheService {

    private static final String CACHE_FOLDER = "appCache";
    private static final String DOCUMENTS_FOLDER = CACHE_FOLDER + "/documents";
    private static final String SUMMARIES_FOLDER = CACHE_FOLDER + "/summaries";
    private static final String QUIZZES_FOLDER = CACHE_FOLDER + "/quizzes";

    public static void setupCacheFolders() {
        createFolder(CACHE_FOLDER);
        createFolder(DOCUMENTS_FOLDER);
        createFolder(SUMMARIES_FOLDER);
        createFolder(QUIZZES_FOLDER);
    }

    private static void createFolder(String folderPath) {
        File folder = new File(folderPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static void saveSummary(String fileName, String summaryText) {
        setupCacheFolders();

        try {
            FileWriter writer = new FileWriter(SUMMARIES_FOLDER + "/" + fileName + ".txt");
            writer.write(summaryText);
            writer.close();

            System.out.println("Summary saved locally.");
        } catch (IOException e) {
            System.out.println("Error saving summary locally: " + e.getMessage());
        }
    }

    public static String loadSummary(String fileName) {
        setupCacheFolders();

        Path path = Path.of(SUMMARIES_FOLDER + "/" + fileName + ".txt");

        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "No cached summary found.";
        }
    }

    public static void saveQuiz(String fileName, String quizText) {
        setupCacheFolders();

        try {
            FileWriter writer = new FileWriter(QUIZZES_FOLDER + "/" + fileName + ".txt");
            writer.write(quizText);
            writer.close();

            System.out.println("Quiz saved locally.");
        } catch (IOException e) {
            System.out.println("Error saving quiz locally: " + e.getMessage());
        }
    }

    public static String loadQuiz(String fileName) {
        setupCacheFolders();

        Path path = Path.of(QUIZZES_FOLDER + "/" + fileName + ".txt");

        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "No cached quiz found.";
        }
    }

    public static void saveDocumentCopy(File originalFile) {
        setupCacheFolders();

        try {
            Path destination = Path.of(DOCUMENTS_FOLDER + "/" + originalFile.getName());
            Files.copy(originalFile.toPath(), destination);

            System.out.println("Document saved locally.");
        } catch (IOException e) {
            System.out.println("Error saving document locally: " + e.getMessage());
        }
    }
}