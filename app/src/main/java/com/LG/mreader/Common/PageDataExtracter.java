package com.LG.mreader.Common;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PageDataExtracter {
    private static String TAG="Common";



    public static String extractMangaData(String html) {
        Document doc = Jsoup.parse(html);

        // homepage (logo or root link)
        String homepage = "";
        Element logo = doc.selectFirst("a.logo, a[href=/]");
        if (logo != null) homepage = logo.absUrl("href");

        // main image source (first reader image)
        String pageSource = "";
        Element pageImg = doc.selectFirst("img#manga-page, img.reader-page, .page img");
        if (pageImg != null) pageSource = pageImg.absUrl("src");

        // title (try <title> first, fallback <h1>)
        String title = "";
        Element titleTag = doc.selectFirst("title");
        if (titleTag != null) title = titleTag.text();
        else {
            Element h1 = doc.selectFirst("h1");
            if (h1 != null) title = h1.text();
        }

        // navigation (next & prev)
        String nextPage = "";
        Element next = doc.selectFirst("a.next, a[rel=next]");
        if (next != null) nextPage = next.absUrl("href");

        String prevPage = "";
        Element prev = doc.selectFirst("a.prev, a[rel=prev]");
        if (prev != null) prevPage = prev.absUrl("href");

        // all images inside chapter-reader
        StringBuilder imgSrcBuilder = new StringBuilder();
        Element reader = doc.getElementById("chapter-reader");
        if (reader != null) {
            Elements imgs = reader.getElementsByTag("img");
            for (int i = 0; i < imgs.size(); i++) {
                if (i > 0) imgSrcBuilder.append(",");
                imgSrcBuilder.append(imgs.get(i).absUrl("src"));
            }
        }

        String imgSrc = imgSrcBuilder.toString();

        // Final concatenated string (same format you wanted)
        return homepage + "~#" + pageSource + "~#" + title + "~#"
                + nextPage + "~#" + prevPage + "~#" + imgSrc;
    }
    public static String fetchNextChapter(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

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
            System.out.println("Response Code: " + status);

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

            conn.disconnect();
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }
}
