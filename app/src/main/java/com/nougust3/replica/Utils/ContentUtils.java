package com.nougust3.replica.Utils;

import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.ArrayList;

public class ContentUtils {

    private static String URL_PREFIX = "file:///" + Environment.getExternalStorageDirectory() + "/Replica" + File.separator;

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

    public static String replaceUrls(String content) {
        ArrayList<String> urlsList = new ArrayList<>();
        String newUrl;

        for (Element element : Jsoup.parse(content).select("div *")) {
            String tagName = element.tagName();
            if ("img".equals(tagName)) {
                urlsList.add(element.attr("src"));
            }
        }

        for (int i = 0; i < urlsList.size(); i++) {
            Log.i("Replica", urlsList.get(i));
            newUrl = urlsList.get(i).replace(",", "/");
            Log.i("Replica", urlsList.get(i));
            content = content.replace(urlsList.get(i), newUrl);
        }

        return content;
    }

    private static ArrayList<String> getUrls(String content) {
        ArrayList<String> urlsList = new ArrayList<>();

        for (Element element : Jsoup.parse(content).select("div *")) {
            String tagName = element.tagName();
            if ("img".equals(tagName)) {
                urlsList.add(element.attr("src"));
            }
        }

        return urlsList;
    }

    public static String updateUrls(String content, boolean state) {
        if(state) {
            if(Preferences.getInstance().get("saveImages")) {  // Загрузка с включенным кэшем
                ArrayList<String> urlsList = getUrls(content);

                for(int i = 0; i < urlsList.size(); i++) {
                    String url;

                    url = urlsList.get(i).replace("/", ",");
                    url = URL_PREFIX + url;

                    content = content.replace(urlsList.get(i), url);
                }
            }
        }
        else {
            if(Preferences.getInstance().get("saveImages")) {
                ArrayList<String> urlsList = getUrls(content);

                for(int i = 0; i < urlsList.size(); i++) {
                    String url;

                    url = urlsList.get(i).replace(URL_PREFIX, "");
                    url = url.replace(",", "/");

                    content = content.replace(urlsList.get(i), url);
                }
            }
        }

        return content;
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
            if (current >= 0xFFF0 && current <= 0xFFFC) {
                current = (" ").charAt(0);
            }
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
