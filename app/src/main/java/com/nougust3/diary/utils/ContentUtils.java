package com.nougust3.diary.Utils;

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

        return cleanInvalidCharacters(text);
    }

    public static String cleanInvalidCharacters(String in) {
        StringBuilder out = new StringBuilder();
        char current;
        if (in == null || ("".equals(in))) {
            return "";
        }
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i);
            if ((current == 0x9)
                    || (current == 0xA)
                    || (current == 0xD)
                    || ((current >= 0x20) && (current <= 0xD7FF))
                    || ((current >= 0xE000) && (current <= 0xFFFD))
                    || ((current >= 0x10000) && (current <= 0x10FFFF))) {
                out.append(current);
            }

        }
        out.toString().replaceAll("[^\\\\uFFF0-\\\\uFFFC]", " ");
        return out.toString().replaceAll("\\s", " ");
    }

}
