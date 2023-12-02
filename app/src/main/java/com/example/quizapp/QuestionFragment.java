package com.example.quizapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizapp.model.QuestionBank;
import com.example.quizapp.model.Questions;

import org.jetbrains.annotations.Nullable;

public class QuestionFragment extends Fragment{
    private QuestionBank questionBank;
    private TextView questionText;
    private Button trueButton;
    private Button falseButton;
    private ProgressBar progressBar;

    private int currentQuestion;

    private int correctAnswers = 0;

    public QuestionFragment(QuestionBank questionBank, int numberOfQuestions) {
        this.questionBank = questionBank;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        // Access UI components from the main activity layout
        trueButton = getActivity().findViewById(R.id.trueButton);
        falseButton = getActivity().findViewById(R.id.falseButton);
        progressBar = getActivity().findViewById(R.id.progressBar);

        // Initialize other UI components from the fragment layout
        questionText = view.findViewById(R.id.questionTextView);

        // Set initial question
        loadNextQuestion();

        // Set click listeners for True and False buttons
        trueButton.setOnClickListener(v -> checkAnswer(true));
        falseButton.setOnClickListener(v -> checkAnswer(false));


        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if there is a saved instance state
        if (savedInstanceState != null) {
            // If yes, restore the saved state
            currentQuestion = savedInstanceState.getInt("currentQuestion", 0);
            correctAnswers = savedInstanceState.getInt("correctAnswers", 0);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Save the current state of the fragment
        outState.putInt("currentQuestion", currentQuestion);
        outState.putInt("correctAnswers", correctAnswers);

        super.onSaveInstanceState(outState);
    }

    private void loadNextQuestion() {

        Questions nextQuestion = questionBank.getNextQuestion();

        if (nextQuestion != null) {
            questionText.setText(nextQuestion.getText());
            questionText.setBackgroundColor(getResources().getColor(nextQuestion.getColor(), null));
        } else {
            // Quiz completed, show result dialog
            showQuizResultDialog(correctAnswers);
        }
    }



    private void checkAnswer(boolean selectedAnswer) {
        Questions currentQuestion = questionBank.getCurrentQuestion();

        if (currentQuestion != null) {
            if (selectedAnswer == currentQuestion.getAnswer()) {
                showToast("Correct!");
                correctAnswers++; // Increment correct answers count
            } else {
                showToast("Incorrect!");
            }
        }

        // Load the next question
        loadNextQuestion();

        // Update progress bar
        updateProgressBar();
    }

    private void updateProgressBar() {
        int totalQuestions = questionBank.getTotalQuestions();
        int remainingQuestions = questionBank.getRemainingQuestions();
        int progress = ((totalQuestions - remainingQuestions) * 100) / totalQuestions;
        progressBar.setProgress(progress);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void showQuizResultDialog(int correctAnswers) {
        ((MainActivity) requireActivity()).showQuizResultDialog(correctAnswers);
    }

}
