package com.example.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.Toast;


import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quizapp.model.QuestionBank;
import com.example.quizapp.model.Questions;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;


public class MainActivity extends AppCompatActivity {

    private QuestionBank questionBank;

    private int selectedNumberOfQuestions = 10; // Default number of questions


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //for saving state

        // Check if there is a saved instance state
        if (savedInstanceState == null) {
            // If not, initialize the quiz for the first attempt
            initializeQuiz();
        } else {
            // If yes, restore the saved state
            questionBank = savedInstanceState.getParcelable("questionBank");
            if (questionBank != null) {
                // If the questionBank is not null, load the question fragment
                loadQuestionFragment();
            } else {
                // If questionBank is null, initialize the quiz
                initializeQuiz();
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("menu", "This is a debug message.");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menuItem1) {
            showAverageReport();
            return true;
        } else if (itemId == R.id.menuItem2) {
            showSelectQuestionsDialog();
            return true;
        } else if (itemId == R.id.menuItem3) {
            resetSavedResults();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // Save the current state of the quiz
        outState.putParcelable("questionBank", questionBank);

        super.onSaveInstanceState(outState);
    }



    private void initializeQuiz() {
        List<Questions> questions = new ArrayList<>();


       questions.add(new Questions(getString(R.string.question_1), true, android.R.color.holo_blue_light));
        questions.add(new Questions(getString(R.string.question_2), false, android.R.color.holo_green_light));
        questions.add(new Questions(getString(R.string.question_3), true, android.R.color.holo_red_light));
        questions.add(new Questions(getString(R.string.question_4), true, android.R.color.holo_orange_light));
        questions.add(new Questions(getString(R.string.question_5), false, android.R.color.holo_purple));
        questions.add(new Questions(getString(R.string.question_6), true, android.R.color.holo_red_light));
        questions.add(new Questions(getString(R.string.question_7), false, android.R.color.holo_purple));
        questions.add(new Questions(getString(R.string.question_8), false, android.R.color.holo_blue_dark));
        questions.add(new Questions(getString(R.string.question_9), false, android.R.color.holo_green_dark));
        questions.add(new Questions(getString(R.string.question_10), true, android.R.color.holo_red_dark));
        questions.add(new Questions(getString(R.string.question_11), true, android.R.color.holo_blue_dark));

        // Initialize question bank
        questionBank = new QuestionBank(questions);

        loadQuestionFragment();
    }



    private void loadQuestionFragment() {
        // Create a new instance of QuestionFragment with the selected number of questions
        QuestionFragment questionFragment = new QuestionFragment(questionBank, selectedNumberOfQuestions);

        // Use FragmentManager to replace the existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, questionFragment);
        fragmentTransaction.commit();
    }


    public void showQuizResultDialog(int correctAnswers) {

        List<String> quizResults = FileStorageManager.getQuizResults(this);

        // Count the number of attempts
        int numberOfAttempts = quizResults.size();

        // Create the result message
        String resultMessage = "Your correct answers: " + correctAnswers +
                " in " + numberOfAttempts + (numberOfAttempts == 1 ? " attempt." : " attempts.");

        // Display the result message in a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Completed");
        builder.setMessage(resultMessage);

        builder.setPositiveButton("Save", (dialog, which) -> {
            // Save the result to the file system
            FileStorageManager.saveResult(this, resultMessage);

            // Reset the quiz for another attempt
            resetQuiz();
        });

        builder.setNegativeButton("Ignore", (dialog, which) -> {
            // Reset the quiz without saving the result
            resetQuiz();
        });

        builder.show();

    }


    private void resetQuiz() {
        // Reset quiz variables
        questionBank.reset();

        // Shuffle questions and colors
        questionBank.shuffleQuestions();

        // Update UI for the new attempt
        loadQuestionFragment();
    }

    // Inside menu

    private void showAverageReport() {

        //Get quiz results from persistent storage
        List<String> quizResults = FileStorageManager.getQuizResults(this);

        if (quizResults.isEmpty()) {
            Toast.makeText(this, "No quiz attempts to calculate average.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate the average score
        double totalScore = 0;
        for (String result : quizResults) {
            // Extract the score from the result
            int score = extractScoreFromResult(result);
            totalScore += score;
        }

        double average = totalScore / quizResults.size();

        // Show the average report dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Average Score Report");
        builder.setMessage("Average Score: " + String.format("%.2f", average));
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private int extractScoreFromResult(String result) {
        try {
            // Split the result string and extract the score part
            String[] parts = result.split(" ");
            if (parts.length >= 4) {
                // Parse the score part to an integer
                return Integer.parseInt(parts[3]);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        // Return a default value or handle the case where extraction fails
        return 0;
    }



    private void showSelectQuestionsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Number of Questions");

        // Create a NumberPicker
        NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10); // Set the maximum number of questions according to your requirements
        numberPicker.setValue(selectedNumberOfQuestions); // Set the current value to the previously selected number

        builder.setView(numberPicker);

        builder.setPositiveButton("Start Quiz", (dialog, which) -> {
            int newSelectedNumberOfQuestions = numberPicker.getValue();
            if (newSelectedNumberOfQuestions != selectedNumberOfQuestions) {
                // Update the selected number only if it's different from the current value
                selectedNumberOfQuestions = newSelectedNumberOfQuestions;
                loadQuestionFragment();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle cancel if needed
        });

        builder.show();


    }


    private void resetSavedResults() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Results");
        builder.setMessage("Are you sure you want to reset all saved results? This action cannot be undone.");

        builder.setPositiveButton("Reset", (dialog, which) -> {
            // Implement logic to reset saved results
            FileStorageManager.clearQuizResults(this);

            // Inform the user that results have been reset
            Toast.makeText(this, "Saved results reset.", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }


}
