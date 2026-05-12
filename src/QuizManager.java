package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



//shuffles the quiz
//Technical Design Breakdown
//Package Structure (org.example): Organizes the logic, likely interacting
// with other components (like UI or networking) not shown here.
//Core Responsibilities (org.example.QuizManager):
//Question State: Manages the active list of QuizQuestion objects
// (questions, options, correct answers).

//Shuffle Limit: Enforces MAX_SHUFFLES = 3 to limit reordering.
//Quota Management: Integrates with a dependency, org.example.QuotaManager, based on the userId passed in the constructor, suggesting user-specific quotas.
//Component Structure:
//QuizQuestion (Inner Class): Data model holding question text,
// answer options, and the correct answer string.

//QuizCallback (Interface): Defines methods for handling success (receiving questions, shuffles left), quota exceeded, or errors.
//ShuffleCallback (Interface): Specifically handles UI updates
// after a shuffle (updated list, remaining shuffles).

//Data Structures: Employs java.util.ArrayList for storing
// questions and java.util.Collections to manipulate them


public class QuizManager {

    public static final int MAX_SHUFFLES = 3;

    private int shuffleCount = 0;
    private List<QuizQuestion> currentQuestions = new ArrayList<>();

    private final QuotaManager quotaManager;

   //Quiz Question model

    public static class QuizQuestion {
        public final String question;
        public final List<String> options;
        public final String correctAnswer;

        public QuizQuestion(String question, List<String> options, String correctAnswer) {
            this.question = question;
            this.options = new ArrayList<>(options);
            this.correctAnswer = correctAnswer;
        }
    }

   //interfaces

    public interface QuizCallback {
        void onSuccess(List<QuizQuestion> questions, int shufflesRemaining);
        void onQuotaExceeded(int usedCount, int maxCount);
        void onError(Exception e);
    }

    public interface ShuffleCallback {
        /**
         * @param shuffledQuestions Reordered list
         * @param shufflesRemaining 0 means the Shuffle button should be hidden
         */
        void onShuffled(List<QuizQuestion> shuffledQuestions, int shufflesRemaining);
        void onMaxShufflesReached();
    }

    //Constructor

    public QuizManager(String userId) {
        this.quotaManager = new QuotaManager(userId);
    }

    //Generate Quiz
    //Generates a new quiz. Counts as 1 quota usage.
    //Resets the shuffle counter to 0 for the new session.


    public void generateQuiz(String topicText, QuizCallback callback) {
        quotaManager.checkAndIncrement(new QuotaManager.QuotaCallback() {
            @Override
            public void onResult(boolean allowed, int usedCount, int maxCount) {
                if (!allowed) {
                    callback.onQuotaExceeded(usedCount, maxCount);
                    return;
                }

                // Reset shuffle counter for new quiz
                shuffleCount = 0;

                callAiForQuiz(topicText, questions -> {
                    currentQuestions = new ArrayList<>(questions);
                    callback.onSuccess(new ArrayList<>(currentQuestions),
                            MAX_SHUFFLES - shuffleCount);
                }, callback::onError);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    //2. Shuffle Quiz (free no quota cost, max 3 times)


    public void shuffleQuiz(ShuffleCallback callback) {
        if (currentQuestions.isEmpty() || shuffleCount >= MAX_SHUFFLES) {
            callback.onMaxShufflesReached();
            return;
        }

        shuffleCount++;

        // Retry shuffle if the result is accidentally identical to current order
        List<QuizQuestion> shuffled = new ArrayList<>(currentQuestions);
        int attempts = 0;
        do {
            Collections.shuffle(shuffled);
            attempts++;
        } while (shuffled.equals(currentQuestions) && attempts < 5);

        currentQuestions = shuffled;

        int remaining = MAX_SHUFFLES - shuffleCount;
        callback.onShuffled(new ArrayList<>(currentQuestions), remaining);
    }

  //Getters

    public int getShufflesRemaining() {
        return Math.max(0, MAX_SHUFFLES - shuffleCount);
    }

    public boolean canShuffle() {
        return shuffleCount < MAX_SHUFFLES && !currentQuestions.isEmpty();
    }

    public List<QuizQuestion> getCurrentQuestions() {
        return new ArrayList<>(currentQuestions);
    }

    //helper


    private void callAiForQuiz(String topicText,
                               java.util.function.Consumer<List<QuizQuestion>> onSuccess,
                               java.util.function.Consumer<Exception> onError) {
        // TODO: Replace with actual AI API call.

        // Placeholder — generates 3 dummy questions for testing:
        List<QuizQuestion> placeholder = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            placeholder.add(new QuizQuestion(
                    "Question " + i + " about: " +
                            topicText.substring(0, Math.min(20, topicText.length())),
                    List.of("A. Option one", "B. Option two",
                            "C. Option three", "D. Option four"),
                    "A"
            ));
        }
        onSuccess.accept(placeholder);
    }
}