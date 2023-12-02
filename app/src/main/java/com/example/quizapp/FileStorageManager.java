package com.example.quizapp;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileStorageManager {

    private static final String RESULTS_FILE_NAME = "quiz_results.txt";

    public static void saveResult(Context context, String result) {
        // Load existing results
        List<String> quizResults = loadResults(context);

        // Add the new result
        quizResults.add(result);

        // Save updated results
        saveResults(context, quizResults);
    }

    public static List<String> getQuizResults(Context context) {
        return loadResults(context);
    }

    public static void saveResults(Context context, List<String> results) {
        // Check if external storage is available and not read-only
        if (isExternalStorageWritable()) {
            File externalFile = new File(context.getExternalFilesDir(null), RESULTS_FILE_NAME);

            try (FileOutputStream fileOutputStream = new FileOutputStream(externalFile)) {
                for (String result : results) {
                    String resultString = result + System.lineSeparator();
                    fileOutputStream.write(resultString.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<String> loadResults(Context context) {
        List<String> quizResults = new ArrayList<>();

        // Check if external storage is available
        if (isExternalStorageReadable()) {
            File externalFile = new File(context.getExternalFilesDir(null), RESULTS_FILE_NAME);

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(externalFile))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    quizResults.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return quizResults;
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static void clearQuizResults(Context context) {
        if (isExternalStorageWritable()) {
            File externalFile = new File(context.getExternalFilesDir(null), "quiz_results.txt");
            if (externalFile.exists()) {
                externalFile.delete();
            }
        }
    }
}
