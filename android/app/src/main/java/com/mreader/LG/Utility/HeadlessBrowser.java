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
    public interface callback {
        void onSuccess(String data);
        void onError(Exception e);
    }

    private static volatile HeadlessBrowser instance;
    private static final String TAG = "HeadlessBrowser";
    private final ContextManager contextManager;

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

    public void fetchData(String url, String payload, callback cb) {
        if (cb == null) {
            return;
        }
        if (url == null || url.isEmpty() || payload == null || payload.isEmpty()) {
            cb.onError(new IllegalArgumentException("url and payload must be non-empty"));
            return;
        }

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            WebView requestWebView = createWebView();
            AtomicBoolean completed = new AtomicBoolean(false);

            requestWebView.setWebViewClient(new WebViewClient() {
                private void finishSuccess(String value) {
                    if (!completed.compareAndSet(false, true)) {
                        return;
                    }
                    try {
                        cb.onSuccess(value);
                    } finally {
                        cleanupWebView(requestWebView);
                    }
                }

                private void finishError(Exception error) {
                    if (!completed.compareAndSet(false, true)) {
                        return;
                    }
                    try {
                        cb.onError(error);
                    } finally {
                        cleanupWebView(requestWebView);
                    }
                }

                @Override
                public void onPageFinished(WebView view, String loadedUrl) {
                    super.onPageFinished(view, loadedUrl);

                    view.evaluateJavascript(
                            "(function(){ return document.title; })();",
                            title -> {
                                if (completed.get()) {
                                    return;
                                }

                                if (title != null && title.toLowerCase().contains("just a moment")) {
                                    Log.d("SCRAPER", "Blocked by Cloudflare");
                                    finishSuccess("[\"BLOCKED\"]");
                                    return;
                                }

                                mainHandler.postDelayed(() -> {
                                    if (completed.get()) {
                                        return;
                                    }

                                    view.evaluateJavascript(
                                            payload,
                                            value -> {
                                                if (value == null) {
                                                    finishError(new IllegalStateException("evaluateJavascript returned null"));
                                                    return;
                                                }

                                                Log.d("SCRAPED_DATA", value);
                                                finishSuccess(value);
                                            }
                                    );
                                }, 9000);
                            }
                    );
                }

                @Override
                public void onReceivedError(
                        WebView view,
                        int errorCode,
                        String description,
                        String failingUrl
                ) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    finishError(new RuntimeException(
                            "WebView load failed (" + errorCode + "): " + description + " for " + failingUrl
                    ));
                }
            });

            requestWebView.loadUrl(url);
        });
    }

    private WebView createWebView() {
        WebView webView = new WebView(contextManager.getApplicationMainContext());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUserAgentString("Mozilla/5.0 Chrome/90.0 Mobile");

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);
        return webView;
    }

    private void cleanupWebView(WebView webView) {
        webView.stopLoading();
        webView.loadUrl("about:blank");
        webView.setWebViewClient(null);
        webView.destroy();
        Log.d(TAG, "Disposed headless WebView request");
    }
}
