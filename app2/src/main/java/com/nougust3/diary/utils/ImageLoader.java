package com.nougust3.diary.Utils;

        import android.annotation.SuppressLint;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.AsyncTask;
        import android.os.Environment;
        import android.util.Log;

        import org.jsoup.Jsoup;
        import org.jsoup.nodes.Element;

        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.net.URL;
        import java.util.ArrayList;

        import com.nougust3.diary.Presenter.EditorPresenter;

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
        checkDirectory();

        File file;
        imagesCount = 0;

        //createUrlsLocal2();

        for(int i = 0; i < urlsList.size(); i++) {
            file = new File(URL_PREFIX + urlsList.get(i));

            if(!file.exists()) {
                DownloadImageTask imageTask = new DownloadImageTask();
                imageTask.execute(urlsList.get(i));
            }
        }
    }

    private void checkDirectory() {
        File directory = new File(Environment.getExternalStorageDirectory() + "/Replica");

        if (!directory.exists()){
            directory.mkdirs();
        }
    }

    private void onImageLoaded() {
        imagesCount++;

        Log.i("Replica", "Loaded " + imagesCount + " from " + urlsList.size());

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
                saveImage(result);
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

    private static File saveImage(ImageWrapper result) throws IOException {
        result.name = result.name.replace("/", ",");

        FileOutputStream oStream;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        File imageFile = new File(Environment.getExternalStorageDirectory() + "/Replica" + File.separator + result.name);

        Log.i("Replica", result.name);
        Log.i("Replica", Environment.getExternalStorageDirectory() + "/Replica" + File.separator + result.name);

        result.bitmap.compress(Bitmap.CompressFormat.WEBP, 40, bytes);

        oStream = new FileOutputStream(imageFile);

        oStream.write(bytes.toByteArray());
        oStream.close();
        return imageFile;
    }

}