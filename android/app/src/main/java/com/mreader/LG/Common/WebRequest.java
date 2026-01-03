package com.mreader.LG.Common;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebRequest {

    private static final String TAG = "WebRequest";

    public static String fetchPageHTML(String urlString, String baseUrl) {
        HttpURLConnection conn=null;
        try {
            URL url = new URL(urlString);
            conn= (HttpURLConnection) url.openConnection();

            // Set HTTP method
            conn.setRequestMethod("GET");

            // Set headers
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            conn.setRequestProperty("Priority", "u=0, i");
            conn.setRequestProperty("Sec-CH-UA", "\"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"");
            conn.setRequestProperty("Sec-CH-UA-Mobile", "?0");
            conn.setRequestProperty("Sec-CH-UA-Platform", "\"Windows\"");
            conn.setRequestProperty("Sec-Fetch-Dest", "document");
            conn.setRequestProperty("Sec-Fetch-Mode", "navigate");
            conn.setRequestProperty("Sec-Fetch-Site", "none");
            conn.setRequestProperty("Sec-Fetch-User", "?1");
            conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/141.0.0.0 Safari/537.36");

            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);

            // Make request
            int status = conn.getResponseCode();
            Log.d(TAG,"Response Code: " + status);

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    (status >= 200 && status < 400) ? conn.getInputStream() : conn.getErrorStream()
            ));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();


            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(conn!=null){
                conn.disconnect();
            }
        }
        return "";
    }
}
