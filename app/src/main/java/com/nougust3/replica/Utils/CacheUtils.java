package com.nougust3.replica.Utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CacheUtils {

    private static String URL_PREFIX = "file:///" + Environment.getExternalStorageDirectory() + "/Replica" + File.separator;

    public static boolean checkImage(String name) {
        File file;

        name = name.replace("/", ",");
        file = new File(URL_PREFIX + name);

        return file.exists();
    }

    public static void checkDirectory() {
        File directory = new File(Environment.getExternalStorageDirectory() + "/Replica");

        if (!directory.exists()){
            directory.mkdirs();
        }
    }

    public static void saveImage(Bitmap image, String name) throws IOException {
        name = name.replace("/", ",");

        FileOutputStream oStream;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        File imageFile = new File(Environment.getExternalStorageDirectory() + "/Replica" + File.separator + name);

        image.compress(Bitmap.CompressFormat.WEBP, 40, bytes);

        oStream = new FileOutputStream(imageFile);
        oStream.write(bytes.toByteArray());
        oStream.close();
    }

    public static long getCacheSize() {
        long length;

        File directory = new File(Environment.getExternalStorageDirectory() + "/Replica");

        length = getFolderSize(directory);

        return length;
    }

    private static long getFolderSize(File directory) {
        long length = 0;

        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += getFolderSize(file);
        }
        return length;
    }

    public void clearCache() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/Replica");

        if(dir.isDirectory()) {
            String[] children = dir.list();

            for (String aChildren : children) {
                new File(dir, aChildren).delete();
            }
        }
    }

}
