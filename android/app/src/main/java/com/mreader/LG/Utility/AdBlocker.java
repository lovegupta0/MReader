package com.mreader.LG.Utility;

import android.webkit.WebResourceResponse;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

public final class AdBlocker {

    private static final Set<String> AD_HOSTS = new HashSet<>();


    static {
        // 🔥 Core ad / tracker domains
        AD_HOSTS.add("doubleclick.net");
        AD_HOSTS.add("googlesyndication.com");
        AD_HOSTS.add("googleadservices.com");
        AD_HOSTS.add("adsystem.com");
        AD_HOSTS.add("ads.yahoo.com");
        AD_HOSTS.add("facebook.com/tr");
        AD_HOSTS.add("analytics.google.com");
        AD_HOSTS.add("stats.g.doubleclick.net");
        AD_HOSTS.add("taboola.com");
        AD_HOSTS.add("outbrain.com");
        AD_HOSTS.add("criteo.com");
        AD_HOSTS.add("adservice");
        AD_HOSTS.add("tracking");
        AD_HOSTS.add("intent://");
        AD_HOSTS.add("https://toffee.ai");
        AD_HOSTS.add("https://junior-shine");
        AD_HOSTS.add("https://click.a-ads.com");
        AD_HOSTS.add("https://landingbc.com/");
    }

    private AdBlocker() {}

    public static boolean isAd(String url) {
        if (url == null) return false;
        for (String host : AD_HOSTS) {
            if (url.contains(host)) return true;
        }
        return false;
    }

    public static WebResourceResponse emptyResponse() {
        return new WebResourceResponse(
                "text/plain",
                "utf-8",
                new ByteArrayInputStream(new byte[0])
        );
    }
}