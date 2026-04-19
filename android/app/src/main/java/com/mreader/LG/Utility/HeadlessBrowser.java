package com.mreader.LG.Utility;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.concurrent.atomic.AtomicBoolean;

public class HeadlessBrowser {

    public interface Callback {
        void onSuccess(String data);
        void onError(Exception e);
    }

    private static final String TAG = "HeadlessBrowser";

    // Timing
    private static final int CF_DETECT_DELAY_MS     = 2000;
    private static final int CF_POLL_INTERVAL_MS    = 3000;
    private static final int CF_POLL_MAX_RETRIES    = 10;
    private static final int SCRAPE_SETTLE_DELAY_MS = 2000;

    private static final String USER_AGENT =
            "Mozilla/5.0 (Linux; Android 13; Pixel 7) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/124.0.0.0 Mobile Safari/537.36";

    private static volatile HeadlessBrowser instance;
    private final ContextManager contextManager;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Warm WebView kept alive between requests for session/cookie continuity
    private WebView warmWebView;
    private String warmWebViewDomain;

    private HeadlessBrowser() {
        contextManager = ContextManager.getInstance();
    }

    public static HeadlessBrowser getInstance() {
        if (instance == null) {
            synchronized (HeadlessBrowser.class) {
                if (instance == null) {
                    instance = new HeadlessBrowser();
                }
            }
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Check whether a valid cf_clearance cookie exists for the given domain.
     * Call this before fetchData() to know if the main WebView has already
     * cleared Cloudflare for this domain.
     */
    public boolean hasClearanceCookie(String domain) {
        String url = "https://" + domain;
        String cookies = CookieManager.getInstance().getCookie(url);

        if (cookies == null || cookies.isEmpty()) {
            Log.w(TAG, "cookie_check | domain=" + domain
                    + " | result=NO_COOKIES (cookie jar is empty for this domain)");
            return false;
        }

        Log.d(TAG, "cookie_check | domain=" + domain
                + " | all_cookies=" + cookies);

        boolean hasClearance = cookies.contains("cf_clearance");
        if (hasClearance) {
            // Extract and print just the cf_clearance value for clarity
            String clearanceValue = extractCookieValue(cookies, "cf_clearance");
            Log.i(TAG, "cookie_check | domain=" + domain
                    + " | result=PRESENT"
                    + " | cf_clearance=" + clearanceValue);
        } else {
            Log.w(TAG, "cookie_check | domain=" + domain
                    + " | result=MISSING"
                    + " | note=User has not passed CF challenge in main WebView yet");
        }

        return hasClearance;
    }

    public void fetchData(String url, String payload, Callback cb) {
        if (cb == null) return;

        if (url == null || url.isEmpty() || payload == null || payload.isEmpty()) {
            cb.onError(new IllegalArgumentException("url and payload must be non-empty"));
            return;
        }

        String domain = extractDomain(url);

        // Log cookie state before attempting fetch
        Log.d(TAG, "fetchData | url=" + url);
        logAllCookies(domain);

        mainHandler.post(() -> {
            WebView webView = getOrCreateWarmWebView(domain);
            AtomicBoolean completed = new AtomicBoolean(false);

            webView.setWebViewClient(new WebViewClient() {

                // -- Helpers --------------------------------------------------

                private void finishSuccess(String value) {
                    if (!completed.compareAndSet(false, true)) return;
                    CookieManager.getInstance().flush();
                    logAllCookies(domain); // log cookies after success
                    try {
                        cb.onSuccess(value);
                    } finally {
                        webView.setWebViewClient(null);
                    }
                }

                private void finishError(Exception error) {
                    if (!completed.compareAndSet(false, true)) return;
                    CookieManager.getInstance().flush();
                    Log.e(TAG, "fetchData | error=" + error.getMessage());
                    try {
                        cb.onError(error);
                    } finally {
                        invalidateWarmWebView();
                    }
                }

                private boolean isCloudflareChallenge(String title) {
                    if (title == null) return false;
                    String lower = title.toLowerCase()
                            .replace("\"", "")
                            .trim();
                    return lower.contains("just a moment") ||
                            lower.contains("checking your browser") ||
                            lower.contains("attention required") ||
                            lower.contains("security check") ||
                            lower.contains("one more step");
                }

                // -- Turnstile polling ----------------------------------------

                private void pollUntilUnblocked(int retriesLeft) {
                    if (completed.get()) return;

                    if (retriesLeft <= 0) {
                        Log.w(TAG, "pollUntilUnblocked | result=TIMEOUT"
                                + " | Turnstile did not resolve after max retries");
                        logAllCookies(domain);
                        finishSuccess("[\"BLOCKED\"]");
                        return;
                    }

                    webView.evaluateJavascript(
                            "(function(){ return document.title; })();",
                            title -> {
                                if (completed.get()) return;

                                Log.d(TAG, "pollUntilUnblocked | retriesLeft=" + retriesLeft
                                        + " | title=" + title);

                                if (isCloudflareChallenge(title)) {
                                    Log.d(TAG, "pollUntilUnblocked | status=STILL_BLOCKED"
                                            + " | retrying in " + CF_POLL_INTERVAL_MS + "ms");
                                    mainHandler.postDelayed(
                                            () -> pollUntilUnblocked(retriesLeft - 1),
                                            CF_POLL_INTERVAL_MS
                                    );
                                } else {
                                    Log.i(TAG, "pollUntilUnblocked | status=RESOLVED"
                                            + " | title=" + title
                                            + " | scraping in " + SCRAPE_SETTLE_DELAY_MS + "ms");
                                    // Cookie may have just been set — log it
                                    logAllCookies(domain);
                                    mainHandler.postDelayed(() -> runPayload(), SCRAPE_SETTLE_DELAY_MS);
                                }
                            }
                    );
                }

                // -- Payload execution ----------------------------------------

                private void runPayload() {
                    if (completed.get()) return;
                    Log.d(TAG, "runPayload | executing JS payload");
                    webView.evaluateJavascript(payload, value -> {
                        if (value == null) {
                            finishError(new IllegalStateException(
                                    "evaluateJavascript returned null"
                            ));
                            return;
                        }
                        Log.d(TAG, "runPayload | result=" + value);
                        finishSuccess(value);
                    });
                }

                // -- WebViewClient overrides ----------------------------------

                @Override
                public void onPageFinished(WebView view, String loadedUrl) {
                    super.onPageFinished(view, loadedUrl);
                    if (completed.get()) return;

                    Log.d(TAG, "onPageFinished | url=" + loadedUrl
                            + " | waiting " + CF_DETECT_DELAY_MS + "ms before title check");

                    mainHandler.postDelayed(() -> {
                        if (completed.get()) return;

                        view.evaluateJavascript(
                                "(function(){ return document.title; })();",
                                title -> {
                                    if (completed.get()) return;

                                    Log.d(TAG, "onPageFinished | title_check | title=" + title);

                                    if (isCloudflareChallenge(title)) {
                                        Log.w(TAG, "onPageFinished | CF_DETECTED"
                                                + " | url=" + loadedUrl
                                                + " | polling for up to "
                                                + (CF_POLL_MAX_RETRIES * CF_POLL_INTERVAL_MS / 1000)
                                                + "s");
                                        CookieManager.getInstance().flush();
                                        logAllCookies(domain);
                                        pollUntilUnblocked(CF_POLL_MAX_RETRIES);
                                    } else {
                                        Log.i(TAG, "onPageFinished | NO_CF_CHALLENGE"
                                                + " | scraping in " + SCRAPE_SETTLE_DELAY_MS + "ms");
                                        mainHandler.postDelayed(() -> runPayload(), SCRAPE_SETTLE_DELAY_MS);
                                    }
                                }
                        );
                    }, CF_DETECT_DELAY_MS);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    Log.e(TAG, "onReceivedError | code=" + errorCode
                            + " | description=" + description
                            + " | url=" + failingUrl);
                    finishError(new RuntimeException(
                            "WebView load failed (" + errorCode + "): "
                                    + description + " for " + failingUrl
                    ));
                }
            });

            webView.loadUrl(url);
            Log.d(TAG, "fetchData | loadUrl=" + url);
        });
    }

    /**
     * Release the warm WebView. Call from your Activity/ViewModel onDestroy().
     */
    public void destroy() {
        mainHandler.post(this::invalidateWarmWebView);
    }

    // -------------------------------------------------------------------------
    // Warm WebView management
    // -------------------------------------------------------------------------

    private WebView getOrCreateWarmWebView(String domain) {
        if (warmWebView != null) {
            if (domain != null && domain.equals(warmWebViewDomain)) {
                Log.d(TAG, "warm_webview | action=REUSE | domain=" + domain);
                return warmWebView;
            } else {
                Log.d(TAG, "warm_webview | action=RECYCLE | old_domain=" + warmWebViewDomain
                        + " | new_domain=" + domain);
                destroyWebView(warmWebView);
            }
        }
        warmWebView = createWebView();
        warmWebViewDomain = domain;
        Log.d(TAG, "warm_webview | action=CREATE | domain=" + domain);
        return warmWebView;
    }

    private void invalidateWarmWebView() {
        if (warmWebView != null) {
            Log.d(TAG, "warm_webview | action=DESTROY | domain=" + warmWebViewDomain);
            destroyWebView(warmWebView);
            warmWebView = null;
            warmWebViewDomain = null;
        }
    }

    // -------------------------------------------------------------------------
    // WebView factory & cleanup
    // -------------------------------------------------------------------------

    private WebView createWebView() {
        WebView webView = new WebView(contextManager.getApplicationMainContext());
        WebSettings s = webView.getSettings();

        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setUserAgentString(USER_AGENT);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setMediaPlaybackRequiresUserGesture(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setDefaultTextEncodingName("UTF-8");

        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(webView, true);
        cm.flush();

        return webView;
    }

    private void destroyWebView(WebView webView) {
        webView.stopLoading();
        webView.loadUrl("about:blank");
        webView.setWebViewClient(null);
        webView.destroy();
        Log.d(TAG, "warm_webview | action=DISPOSED");
    }

    // -------------------------------------------------------------------------
    // Cookie logging helpers
    // -------------------------------------------------------------------------

    /**
     * Logs every cookie for the domain in a readable format.
     * Each cookie is printed on its own line for easy logcat filtering.
     */
    private void logAllCookies(String domain) {
        if (domain == null) return;
        String url = "https://" + domain;
        String raw = CookieManager.getInstance().getCookie(url);

        if (raw == null || raw.isEmpty()) {
            Log.w(TAG, "cookies | domain=" + domain + " | NONE");
            return;
        }

        String[] pairs = raw.split(";");
        Log.d(TAG, "cookies | domain=" + domain + " | count=" + pairs.length);
        for (String pair : pairs) {
            String trimmed = pair.trim();
            boolean isClearance = trimmed.startsWith("cf_clearance");
            String label = isClearance ? "★ cf_clearance" : "  cookie";
            Log.d(TAG, "cookies | " + label + " → " + trimmed);
        }
    }

    /**
     * Extracts the value of a named cookie from a raw cookie string.
     */
    private String extractCookieValue(String rawCookies, String name) {
        for (String pair : rawCookies.split(";")) {
            String trimmed = pair.trim();
            if (trimmed.startsWith(name + "=")) {
                return trimmed.substring(name.length() + 1);
            }
        }
        return "NOT_FOUND";
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String extractDomain(String url) {
        try {
            java.net.URL parsed = new java.net.URL(url);
            return parsed.getHost();
        } catch (Exception e) {
            Log.e(TAG, "extractDomain | failed to parse url=" + url);
            return null;
        }
    }
}