package com.LG.mreader.Middleware;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImgUtil {

    public void getImageDimensionsFromUrl(String imageUrl) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            int imageWidth = connection.getHeaderFieldInt("Width", -1);
            int imageHeight = connection.getHeaderFieldInt("Height", -1);

            Log.d("hello", "Width: " + imageWidth + ", Height: " + imageHeight);

            // Now you have the original width and height of the image from the URL
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
