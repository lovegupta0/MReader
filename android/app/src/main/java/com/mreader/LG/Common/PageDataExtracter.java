package com.mreader.LG.Common;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PageDataExtracter {
    private static String TAG = "PageDataExtracter";

    public static List<String> ExtractDataForChapter(String html, String baseUrl) {

        List<String> lst=new ArrayList<>();
        // Parse with base URL so absUrl() works correctly
        Document doc = Jsoup.parse(html, baseUrl);
        // Cover image extraction
        String cover = "";

        Elements coverDivs = doc.getElementsByClass("cover");
        if (!coverDivs.isEmpty()) {
            Elements imgs = coverDivs.get(0).getElementsByTag("img");
            if (!imgs.isEmpty()) {
                cover = imgs.get(0).absUrl("data-src"); // mirrors currentSrc
            }
        }

// Total chapter extraction
        String data = "";

        Elements totalChapter = doc.getElementsByClass("chapter-number");

        if (!totalChapter.isEmpty()) {
            data = totalChapter.get(0).text().trim();// mirrors innerText
        }
        lst.add(cover);
        int pos = data.indexOf(" ");
        if (pos != -1) {
            lst.add(data.substring(0,pos));
            lst.add(data.substring(pos+1));
        }

        return lst;
    }


    /**
     * Extract manga data matching the JavaScript logic with fallback selectors
     */
    private static String extractMangaData(String html, String baseUrl,String rootUrl) {
        // Parse with base URL so absUrl() works correctly
        Document doc = Jsoup.parse(html, baseUrl);

        // 1️⃣ Homepage - try multiple selectors
        String homepage = "";

        // Try primary selector: .titles h1 a
        Element aTag = doc.selectFirst(".titles h1 a");
        if (aTag != null) {
            homepage = aTag.absUrl("href");
        }

        // Fallback 1: Try any h1 a
        if (homepage.isEmpty()) {
            aTag = doc.selectFirst("h1 a");
            if (aTag != null) {
                homepage = aTag.absUrl("href");
            }
        }

        // Fallback 2: Try .nav-logo or logo link
        if (homepage.isEmpty()) {
            Element logoLink = doc.selectFirst(".nav-logo a, a.logo, .logo a");
            if (logoLink != null) {
                homepage = logoLink.absUrl("href");
            }
        }

        // Fallback 3: Extract from baseUrl
        if (homepage.isEmpty() && baseUrl != null && !baseUrl.isEmpty()) {
            try {
                URL url = new URL(baseUrl);
                homepage = url.getProtocol() + "://" + url.getHost();
            } catch (Exception e) {
                homepage = baseUrl;
            }
        }

        // 2️⃣ Title - try multiple selectors
        String title = "";
        if (aTag != null) {
            title = aTag.text().trim();
        }

        // Fallback 1: Try title tag
        if (title.isEmpty()) {
            Element titleTag = doc.selectFirst("title");
            if (titleTag != null) {
                title = titleTag.text().trim()
                        .replaceAll("\\s*-\\s*Read.*", "")
                        .replaceAll("\\s*\\|.*", "")
                        .trim();
            }
        }

        // Fallback 2: Try any h1
        if (title.isEmpty()) {
            Element h1 = doc.selectFirst("h1");
            if (h1 != null) {
                title = h1.text().trim();
            }
        }

        // 3️⃣ Next page link - try multiple selectors
        String nextPage = "";

        // Try primary: a.next, a[rel="next"]
        Element nextLink = doc.selectFirst("a.next, a[rel=next]");
        if (nextLink != null && !nextLink.hasClass("isDisabled") && !nextLink.hasClass("disabled")) {
            nextPage = nextLink.absUrl("href");
        }

        // Fallback 1: Try .nextchap
        if (nextPage.isEmpty()) {
            nextLink = doc.selectFirst("a.nextchap");
            if (nextLink != null && !nextLink.hasClass("isDisabled") && !nextLink.hasClass("disabled")) {
                nextPage = nextLink.absUrl("href");
            }
        }

        // Fallback 2: Try common navigation patterns
        if (nextPage.isEmpty()) {
            nextLink = doc.selectFirst("a.next-chapter, a.chapter-next, a[title*=next i], a[title*=Next i]");
            if (nextLink != null && !nextLink.hasClass("isDisabled") && !nextLink.hasClass("disabled")) {
                nextPage = nextLink.absUrl("href");
            }
        }

        // 4️⃣ Previous page link - try multiple selectors
        String prevPage = "";

        // Try primary: a.prev, a[rel="prev"]
        Element prevLink = doc.selectFirst("a.prev, a[rel=prev]");
        if (prevLink != null && !prevLink.hasClass("isDisabled") && !prevLink.hasClass("disabled")) {
            prevPage = prevLink.absUrl("href");
        }

        // Fallback 1: Try .prevchap
        if (prevPage.isEmpty()) {
            prevLink = doc.selectFirst("a.prevchap");
            if (prevLink != null && !prevLink.hasClass("isDisabled") && !prevLink.hasClass("disabled")) {
                prevPage = prevLink.absUrl("href");
            }
        }

        // Fallback 2: Try common navigation patterns
        if (prevPage.isEmpty()) {
            prevLink = doc.selectFirst("a.prev-chapter, a.chapter-prev, a[title*=prev i], a[title*=Previous i]");
            if (prevLink != null && !prevLink.hasClass("isDisabled") && !prevLink.hasClass("disabled")) {
                prevPage = prevLink.absUrl("href");
            }
        }

        // 5️⃣ Get all images inside #chapter-reader
        StringBuilder allImages = new StringBuilder();
        String pageSource = baseUrl;

        Element reader = doc.getElementById("chapter-reader");
        if (reader != null) {
            Elements imgs = reader.select("img");

            // Collect all image URLs (matches: img[i].currentSrc || img[i].src)
            for (int i = 0; i < imgs.size(); i++) {
                if (i > 0) {
                    allImages.append(",");
                }

                // Try multiple image source attributes
                String src = imgs.get(i).attr("data-src");
                if (src.isEmpty()) {
                    src = imgs.get(i).attr("data-lazy-src");
                }
                if (src.isEmpty()) {
                    src = imgs.get(i).attr("data-original");
                }
                if (src.isEmpty()) {
                    src = imgs.get(i).absUrl("src");
                }

                // Make absolute URL if relative
                if (!src.isEmpty() && !src.startsWith("http")) {
                    try {
                        URL baseURL = new URL(baseUrl);
                        URL absoluteURL = new URL(baseURL, src);
                        src = absoluteURL.toString();
                    } catch (Exception e) {
                        // Keep original if URL parsing fails
                    }
                }

                allImages.append(src);
            }
        }
        pageSource=homepage;
        homepage=rootUrl;

        // 6️⃣ Final concatenation (exact same structure as JS version)
        String result = homepage + "~#" + pageSource + "~#" + title + "~#"
                + nextPage + "~#" + prevPage + "~#" + allImages;


        return result;
    }

    /**
     * Overload: Extract base URL from HTML if available
     */
    private static String extractMangaData(String html) {
        Document doc = Jsoup.parse(html);
        String baseUrl = "";

        // Try canonical link
        Element canonical = doc.selectFirst("link[rel=canonical]");
        if (canonical != null) {
            baseUrl = canonical.absUrl("href");
        }

        // Try og:url
        if (baseUrl.isEmpty()) {
            Element ogUrl = doc.selectFirst("meta[property=og:url]");
            if (ogUrl != null) {
                baseUrl = ogUrl.attr("content");
            }
        }

        // Try to get from any image src
        if (baseUrl.isEmpty()) {
            Element firstImg = doc.selectFirst("#chapter-reader img, img");
            if (firstImg != null) {
                String src = firstImg.absUrl("src");
                if (src.startsWith("http")) {
                    try {
                        URL url = new URL(src);
                        baseUrl = url.getProtocol() + "://" + url.getHost();
                    } catch (Exception e) {
                        baseUrl = "https://www.mgeko.cc";
                    }
                }
            }
        }

        return extractMangaData(html, baseUrl, baseUrl);
    }

    /**
     * Fetch next chapter HTML and extract data
     */
    public static String fetchNextChapter(String urlString, String baseUrl) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            conn.setRequestProperty("Cache-Control", "max-age=0");
            conn.setRequestProperty("Sec-CH-UA", "\"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"");
            conn.setRequestProperty("Sec-CH-UA-Mobile", "?0");
            conn.setRequestProperty("Sec-CH-UA-Platform", "\"Windows\"");
            conn.setRequestProperty("Sec-Fetch-Dest", "document");
            conn.setRequestProperty("Sec-Fetch-Mode", "navigate");
            conn.setRequestProperty("Sec-Fetch-Site", "same-origin");
            conn.setRequestProperty("Sec-Fetch-User", "?1");
            conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36");
            conn.setRequestProperty("Referer", baseUrl);

            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);


            int status = conn.getResponseCode();
            Log.d(TAG, "Response Code: " + status);

            if (status >= 200 && status < 400) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
                in.close();

                return extractMangaData(response.toString(), urlString,baseUrl);
            } else {
                Log.e(TAG, "HTTP Error: " + status);
                return "";
            }

        } catch (Exception e) {
            Log.e(TAG, "Error fetching chapter", e);
            e.printStackTrace();
            return "";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}