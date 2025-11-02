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

    private static String extractMangaData(String html, String baseUrl) {
        // Parse with base URL so absUrl() works correctly
        Document doc = Jsoup.parse(html, baseUrl);

        // 1️⃣ Homepage (logo link)
        String homepage = "";
        Element logoLink = doc.selectFirst(".nav-logo a");
        if (logoLink != null) {
            homepage = logoLink.absUrl("href");
        }

        // 2️⃣ Title (prefer <title>, fallback to <h1>)
        String title = "";
        Element titleTag = doc.selectFirst("title");
        if (titleTag != null && !titleTag.text().isEmpty()) {
            title = titleTag.text().trim();
        } else {
            Element h1 = doc.selectFirst("h1");
            if (h1 != null) {
                title = h1.text().trim();
            }
        }

        // 3️⃣ Next page link (skip disabled links)
        String nextPage = "";
        Element nextLink = doc.selectFirst("a.nextchap:not(.isDisabled)");
        if (nextLink != null) {
            nextPage = nextLink.absUrl("href");
        }

        // 4️⃣ Previous page link (skip disabled links)
        String prevPage = "";
        Element prevLink = doc.selectFirst("a.prevchap:not(.isDisabled)");
        if (prevLink != null) {
            prevPage = prevLink.absUrl("href");
        }

        // 5️⃣ Page source (base URL from first image)
        String pageSource = "";

        // 6️⃣ All image sources inside #chapter-reader
        StringBuilder allImages = new StringBuilder();

        Element reader = doc.getElementById("chapter-reader");
        if (reader != null) {
            Elements imgs = reader.select("img");

            // Get base URI from first image's src
            if (!imgs.isEmpty()) {
                String firstSrc = imgs.get(0).absUrl("src");
                if (!firstSrc.isEmpty()) {
                    // Extract base URI (everything up to the last '/')
                    int lastSlash = firstSrc.lastIndexOf('/');
                    if (lastSlash > 0) {
                        pageSource = firstSrc.substring(0, lastSlash + 1);
                    } else {
                        pageSource = firstSrc;
                    }
                }
            }

            // Collect all image URLs
            for (int i = 0; i < imgs.size(); i++) {
                if (i > 0) {
                    allImages.append(",");
                }
                String src = imgs.get(i).absUrl("src");
                allImages.append(src);
            }
        }

        // 7️⃣ Final concatenation (same structure as JS version)
        return homepage + "~#" + pageSource + "~#" + title + "~#"
                + nextPage + "~#" + prevPage + "~#" + allImages;
    }

    /**
     * Overload: Extract base URL from HTML if available
     */
    private static String extractMangaData(String html) {
        // Try to extract base URL from canonical link or og:url
        Document doc = Jsoup.parse(html);
        String baseUrl = "";

        Element canonical = doc.selectFirst("link[rel=canonical]");
        if (canonical != null) {
            baseUrl = canonical.attr("href");
            // Get just the domain part
            if (baseUrl.contains("/reader/")) {
                baseUrl = baseUrl.substring(0, baseUrl.indexOf("/reader/"));
            }
        }

        if (baseUrl.isEmpty()) {
            Element ogUrl = doc.selectFirst("meta[property=og:url]");
            if (ogUrl != null) {
                baseUrl = ogUrl.attr("content");
                if (baseUrl.contains("/manga/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.indexOf("/manga/"));
                }
            }
        }

        // Fallback: try to guess from image sources
        if (baseUrl.isEmpty()) {
            Element firstImg = doc.selectFirst("#chapter-reader img");
            if (firstImg != null) {
                String src = firstImg.attr("src");
                if (src.startsWith("http")) {
                    // Extract domain from absolute image URL won't help
                    // Use a reasonable default
                    baseUrl = "https://www.mgeko.cc";
                }
            }
        }

        return extractMangaData(html, baseUrl);
    }

    public static String fetchNextChapter(String urlString,String baseUrl){
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


            return extractMangaData(response.toString());

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
