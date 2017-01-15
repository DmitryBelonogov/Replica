package com.nougust3.diary.utils;

public class ContentUtils {

    public static String htmlToText(String html) {
        String text;

        text = html.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
        text = text.substring(0, text.length() > 160 ? 160 : text.length());

        return text;
    }

}
