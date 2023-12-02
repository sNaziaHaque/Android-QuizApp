package com.example.quizapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank implements Parcelable {

    private List<Questions> questions;
    private int currentIndex;
    private List<Questions> allQuestions;
    //private List<Questions> selectedQuestions;
    private int selectedNumberOfQuestions;


    public QuestionBank(List<Questions> questions) {
        this.questions = new ArrayList<>(questions);
        shuffleQuestions();
        currentIndex = 0;

        this.allQuestions = new ArrayList<>(questions);
        //this.selectedQuestions = new ArrayList<>(questions);
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        selectedNumberOfQuestions = numberOfQuestions;
    }

    public Questions getNextQuestion() {
        if (currentIndex < questions.size()) {
            return questions.get(currentIndex++);
        } else {
            return null; // No more questions
        }
    }

    public void reset() {
        shuffleQuestions();
        currentIndex = 0;
    }

    public void shuffleQuestions() {
        Collections.shuffle(questions);
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public int getRemainingQuestions() {
        return questions.size() - currentIndex;
    }

    // Parcelable implementation
    protected QuestionBank(Parcel in) {
        questions = in.createTypedArrayList(Questions.CREATOR);
        currentIndex = in.readInt();
    }

    public static final Creator<QuestionBank> CREATOR = new Creator<QuestionBank>() {
        @Override
        public QuestionBank createFromParcel(Parcel in) {
            return new QuestionBank(in);
        }

        @Override
        public QuestionBank[] newArray(int size) {
            return new QuestionBank[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(questions);
        dest.writeInt(currentIndex);
    }



    public Questions getCurrentQuestion() {
        if (currentIndex >= 0 && currentIndex < questions.size()) {
            return questions.get(currentIndex);
        } else {
            return null;
        }
    }
    public List<Questions> getQuestions() {
        // Return a subset of questions based on selectedNumberOfQuestions
        if (selectedNumberOfQuestions > 0 && selectedNumberOfQuestions <= allQuestions.size()) {
            return allQuestions.subList(0, selectedNumberOfQuestions);
        } else {
            return allQuestions;  // Return all questions if the selection is invalid
        }
    }


}
