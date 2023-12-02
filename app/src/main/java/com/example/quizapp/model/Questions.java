package com.example.quizapp.model;

import android.os.Parcelable;
import android.os.Parcel;

public class Questions implements Parcelable{

    private String text;
    private boolean answer;
    private int color;

    public Questions(String text, boolean answer, int color) {
        this.text = text;
        this.answer = answer;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public boolean getAnswer() {
        return answer;
    }

    public int getColor() {
        return color;
    }

    // Parcelable implementation
    protected Questions(Parcel in) {
        text = in.readString();
        answer = in.readByte() != 0;
        color = in.readInt();
    }

    public static final Creator<Questions> CREATOR = new Creator<Questions>() {
        @Override
        public Questions createFromParcel(Parcel in) {
            return new Questions(in);
        }

        @Override
        public Questions[] newArray(int size) {
            return new Questions[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeByte((byte) (answer ? 1 : 0));
        dest.writeInt(color);
    }
}
