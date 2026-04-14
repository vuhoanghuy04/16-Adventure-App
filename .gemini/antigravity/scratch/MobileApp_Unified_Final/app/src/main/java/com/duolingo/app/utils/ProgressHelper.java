package com.duolingo.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class ProgressHelper {

    private static final String PREF_NAME = "VocaVerseProgress";
    private static final String KEY_VOCAB_COMPLETED = "vocab_completed_set";
    private static final String KEY_GRAMMAR_COMPLETED = "grammar_completed_set";
    private static final String KEY_GRAMMAR_SCORE_PREFIX = "grammar_score_";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // --- VOCABULARY ---
    public static void markVocabCompleted(Context context, int lessonId) {
        SharedPreferences prefs = getPrefs(context);
        Set<String> set = prefs.getStringSet(KEY_VOCAB_COMPLETED, new HashSet<>());
        Set<String> newSet = new HashSet<>(set);
        newSet.add(String.valueOf(lessonId));
        prefs.edit().putStringSet(KEY_VOCAB_COMPLETED, newSet).apply();
    }

    public static boolean isVocabCompleted(Context context, int lessonId) {
        Set<String> set = getPrefs(context).getStringSet(KEY_VOCAB_COMPLETED, new HashSet<>());
        return set.contains(String.valueOf(lessonId));
    }

    public static int getCompletedVocabCount(Context context) {
        return getPrefs(context).getStringSet(KEY_VOCAB_COMPLETED, new HashSet<>()).size();
    }

    // --- GRAMMAR ---
    public static void markGrammarCompleted(Context context, String category, int score, int maxScore) {
        SharedPreferences prefs = getPrefs(context);
        Set<String> set = prefs.getStringSet(KEY_GRAMMAR_COMPLETED, new HashSet<>());
        Set<String> newSet = new HashSet<>(set);
        newSet.add(category);
        
        prefs.edit()
            .putStringSet(KEY_GRAMMAR_COMPLETED, newSet)
            .putInt(KEY_GRAMMAR_SCORE_PREFIX + category, score)
            .putInt(KEY_GRAMMAR_SCORE_PREFIX + category + "_max", maxScore)
            .apply();
    }

    public static boolean isGrammarCompleted(Context context, String category) {
        Set<String> set = getPrefs(context).getStringSet(KEY_GRAMMAR_COMPLETED, new HashSet<>());
        return set.contains(category);
    }

    public static int getGrammarScore(Context context, String category) {
        return getPrefs(context).getInt(KEY_GRAMMAR_SCORE_PREFIX + category, -1);
    }
    
    public static int getGrammarMaxScore(Context context, String category) {
        return getPrefs(context).getInt(KEY_GRAMMAR_SCORE_PREFIX + category + "_max", -1);
    }

    public static int getCompletedGrammarCount(Context context) {
        return getPrefs(context).getStringSet(KEY_GRAMMAR_COMPLETED, new HashSet<>()).size();
    }
}
