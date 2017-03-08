package com.nougust3.replica.Utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import com.nougust3.replica.Presenter.EditorPresenter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ImageLoader {

    private String URL_PREFIX = "file:///" + Environment.getExternalStorageDirectory() + "/Replica" + File.separator;

    private ImageLoaderInterface anInterface;

    private ArrayList<String> urlsList;
    private int imagesCount = 0;

    @SuppressLint("StaticFieldLeak")
    private static ImageLoader instance = null;

    public static synchronized ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }

        return instance;
    }

    public void setListener(EditorPresenter presenter) {
        anInterface = presenter;
    }

    public ArrayList<String> getUrls(String content) {
        urlsList = new ArrayList<>();

        for (Element element : Jsoup.parse(content).select("div *")) {
            String tagName = element.tagName();
            if ("img".equals(tagName)) {
                urlsList.add(element.attr("src"));
            }
        }

        return urlsList;
    }

    private String getLocalUrl(String url) {
        return URL_PREFIX + url;
    }

    private String repairUrls(String content) {
        content = content.replace(URL_PREFIX, "");
        content = content.replace(",", "/");

        return content;
    }

    public String getUrlsOriginal(String content) {

        getUrls(content);

        for (int i = 0; i < urlsList.size(); i++) {
            content = content.replace(urlsList.get(i), getLocalUrl(urlsList.get(i).replace(URL_PREFIX, "")));
            //content = content.replace(",", "/");
        }

        return content;
    }

    public String createUrlsLocal(String content) {

        getUrls(content);

        for (int i = 0; i < urlsList.size(); i++) {
            content = content.replace(urlsList.get(i), getLocalUrl(urlsList.get(i).replace("/", ",")));
            //content = content.replace(",", "/");
        }

        return content;
    }

    private void createUrlsLocal2() {

        for (int i = 0; i < urlsList.size(); i++) {
            urlsList.set(i, urlsList.get(i).replace("/", ","));
        }

    }

    public void loadImages() {
        CacheUtils.checkDirectory();

        imagesCount = 0;

        for(int i = 0; i < urlsList.size(); i++) {
            if(!CacheUtils.checkImage(urlsList.get(i))) {
                new DownloadImageTask().execute(urlsList.get(i));
            }
        }
    }

    private void onImageLoaded() {
        imagesCount++;

        if(imagesCount == urlsList.size()) {
            anInterface.onLoadingDone();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, ImageWrapper> {

        protected ImageWrapper doInBackground(String... urls) {
            Bitmap bitmap = null;

            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(urls[0]).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new ImageWrapper(bitmap, urls[0]);
        }

        protected void onPostExecute(ImageWrapper result) {
            try {
                CacheUtils.saveImage(result.bitmap, result.name);
            } catch (IOException e) {
                e.printStackTrace();
            }

            onImageLoaded();
        }
    }

    private class ImageWrapper {
        public Bitmap bitmap;
        public String name;

        ImageWrapper(Bitmap bitmap, String name) {
            this.bitmap = bitmap;
            this.name = name;
        }
    }

}