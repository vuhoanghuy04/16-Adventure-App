package com.duolingo.app.utils;

import android.text.Html;
import android.text.Spanned;

public class MarkdownHelper {
    public static Spanned parse(String text) {
        if (text == null) return Html.fromHtml("");
        
        // Convert **bold** to <b>bold</b>
        String processed = text.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        // Convert *italic* to <i>italic</i>
        processed = processed.replaceAll("\\*(.*?)\\*", "<i>$1</i>");
        // Convert new lines to <br>
        processed = processed.replace("\n", "<br>");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(processed, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(processed);
        }
    }
}
