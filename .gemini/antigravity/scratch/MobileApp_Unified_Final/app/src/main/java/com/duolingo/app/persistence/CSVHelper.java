package com.duolingo.app.persistence;

import android.content.Context;
import android.util.Log;

import com.duolingo.app.models.VocabularyItem;
import com.duolingo.app.models.GrammarQuestion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {
    private static final String TAG = "CSVHelper";

    public static List<VocabularyItem> readVocabularyFromCSV(Context context, String fileName, String languageName) {
        List<VocabularyItem> vocabularyList = new ArrayList<>();
        // Ép đọc bằng chuẩn UTF-8 để không bị lỗi font Tiếng Việt
        try (InputStream is = context.getAssets().open(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            
            String line;
            reader.readLine(); // Bỏ qua tiêu đề

            int count = 0;
            while ((line = reader.readLine()) != null) {
                // Tách cột bằng dấu phẩy, xử lý cả trường hợp có dấu phẩy trong ngoặc kép
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                if (tokens.length >= 4) {
                    String word = cleanToken(tokens[0]);
                    String meaning = cleanToken(tokens[1]);
                    String example = cleanToken(tokens[2]);
                    String category = cleanToken(tokens[3]);
                    
                    // Logic tự động phân bài học: mỗi 20 từ là 1 bài
                    // count bắt đầu từ 0, 20 từ đầu là bài 1 (0/20 = 0), tiếp theo là bài 2...
                    int lessonNumber = (count / 20) + 1;
                    
                    // Sử dụng category trong CSV nếu có, nếu không thì dùng "Lesson X"
                    String finalCategory = (category != null && !category.isEmpty()) ? category : ("Lesson " + lessonNumber);
                    
                    vocabularyList.add(new VocabularyItem(word, meaning, example, languageName, finalCategory, lessonNumber));
                    count++;
                }
            }
            Log.d(TAG, "Đã đọc thành công " + vocabularyList.size() + " từ vựng.");
        } catch (IOException e) {
            Log.e(TAG, "Lỗi đọc file: " + e.getMessage());
        }
        return vocabularyList;
    }

    public static List<GrammarQuestion> readGrammarFromCSV(Context context, String fileName) {
        List<GrammarQuestion> questions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName), StandardCharsets.UTF_8))) {
            String line;
            reader.readLine(); // Bỏ qua dòng tiêu đề
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 12) {
                    questions.add(new GrammarQuestion(
                            tokens[0], tokens[1], tokens[2], tokens[3], tokens[4],
                            tokens[5], tokens[6], tokens[7], tokens[8], tokens[9], tokens[10], tokens[11]
                    ));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return questions;
    }

    private static String cleanToken(String token) {
        if (token == null) return "";
        String clean = token.trim();
        if (clean.startsWith("\"") && clean.endsWith("\"")) {
            clean = clean.substring(1, clean.length() - 1);
        }
        return clean.replace("\"\"", "\"");
    }
}
