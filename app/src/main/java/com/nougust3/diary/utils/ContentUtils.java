package com.nougust3.diary.utils;

import android.os.Build;
import android.text.Html;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class ContentUtils {

    public static String htmlToText(String html) {
        String text;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            text = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT).toString();
        }
        else {
            text = Html.fromHtml(html).toString();
        }

        return removeTags(text);
    }

    private static String removeTags(String input) {
        String text;

        text = input.substring(0, input.length() > 160 ? 160 : input.length());
        text = text.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");

        return text;
    }

}
