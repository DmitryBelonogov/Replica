package com.nougust3.diary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageLoader {

    public static String extractUrls(String content, long creation) {
        String newContent = content;

        File directory = new File(Environment.getExternalStorageDirectory()+"/Replica");
        if (!directory.exists()){
            directory.mkdirs();
        }

        Document document = Jsoup.parse(content);
        Elements elements = document.select("div *");
        List<String> values = new ArrayList<>();
        for (Element element : elements) {
            String tagName = element.tagName();
            if ("img".equals(tagName)) {
                values.add(element.attr("src"));
            }
        }
        int i = 0;
        for (String a: values) {
            if(!a.contains("file:///")) {
                DownloadImageTask imageTask = new DownloadImageTask();
                imageTask.setName("img_" + creation + "_" + i + ".webp");
                imageTask.execute(a);

                i++;
            }

            //new DownloadImageTask().execute("http://example.com/image.png");
        }

        i = 0;
        for (String a: values) {
            newContent = newContent.replace(a, "file:///" + Environment.getExternalStorageDirectory() + "/Replica" + File.separator  + "img_" + creation + "_" + i + ".webp");
            i++;
        }


        return newContent;
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        protected Bitmap doInBackground(String... urls) {
            return loadImageFromNetwork(urls[0]);
        }

        protected void onPostExecute(Bitmap result) {
            try {
                saveImage(result, name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static File saveImage(Bitmap bmp, String name) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.WEBP, 40, bytes); File root = Environment.getExternalStorageDirectory();
        File f = new File(Environment.getExternalStorageDirectory() + "/Replica" + File.separator + name);
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;
    }

    private static Bitmap loadImageFromNetwork(String url){
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
