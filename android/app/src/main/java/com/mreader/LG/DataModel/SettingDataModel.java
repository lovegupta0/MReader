package com.mreader.LG.DataModel;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;


import com.mreader.LG.Common.SettingsTypeConverters;

@Entity(tableName = "LGSetting")
@TypeConverters({SettingsTypeConverters.class})
public class SettingDataModel implements Serializable {

    @PrimaryKey
    @NonNull
    private String id = "LG1000";

    private General generalSection;
    private Privacy privacySection;
    private Performance performanceSection;
    private Download downloadSection;
    private Advanced advancedSection;

    // ----------- Getters & Setters -----------
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public General getGeneralSection() { return generalSection; }
    public void setGeneralSection(General generalSection) { this.generalSection = generalSection; }

    public Privacy getPrivacySection() { return privacySection; }
    public void setPrivacySection(Privacy privacySection) { this.privacySection = privacySection; }

    public Performance getPerformanceSection() { return performanceSection; }
    public void setPerformanceSection(Performance performanceSection) { this.performanceSection = performanceSection; }

    public Download getDownloadSection() { return downloadSection; }
    public void setDownloadSection(Download downloadSection) { this.downloadSection = downloadSection; }

    public Advanced getAdvancedSection() { return advancedSection; }
    public void setAdvancedSection(Advanced advancedSection) { this.advancedSection = advancedSection; }

    @Override
    public String toString() {
        return "SettingDataModel{" +
                "id='" + id + '\'' +
                ", generalSection=" + generalSection +
                ", privacySection=" + privacySection +
                ", performanceSection=" + performanceSection +
                ", downloadSection=" + downloadSection +
                ", advancedSection=" + advancedSection +
                '}';
    }

    // --------------------------------------------------------
    // ✅ NESTED PUBLIC STATIC SECTION CLASSES
    // --------------------------------------------------------

    // ---------- General Section ----------
    public static class General implements Serializable {
        private String homepage;
        private String searchEngine;
        private String language;
        private boolean enableViewMode;
        private boolean restoreSession;
        private boolean tabsOpenInBackground;

        public String getHomepage() { return homepage; }
        public void setHomepage(String homepage) { this.homepage = homepage; }

        public String getSearchEngine() { return searchEngine; }
        public void setSearchEngine(String searchEngine) { this.searchEngine = searchEngine; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }

        public boolean isEnableViewMode() { return enableViewMode; }
        public void setEnableViewMode(boolean enableViewMode) { this.enableViewMode = enableViewMode; }

        public boolean isRestoreSession() { return restoreSession; }
        public void setRestoreSession(boolean restoreSession) { this.restoreSession = restoreSession; }

        public boolean isTabsOpenInBackground() { return tabsOpenInBackground; }
        public void setTabsOpenInBackground(boolean tabsOpenInBackground) { this.tabsOpenInBackground = tabsOpenInBackground; }

        @Override
        public String toString() {
            return "General{" +
                    "homepage='" + homepage + '\'' +
                    ", searchEngine='" + searchEngine + '\'' +
                    ", language='" + language + '\'' +
                    ", enableViewMode=" + enableViewMode +
                    ", restoreSession=" + restoreSession +
                    ", tabsOpenInBackground=" + tabsOpenInBackground +
                    '}';
        }
    }

    // ---------- Privacy Section ----------
    public static class Privacy implements Serializable {
        private boolean enableJavaScript;
        private String trackingProtection;
        private String cookiePolicy;
        private boolean httpsOnly;
        private boolean doNotTrack;

        public boolean isEnableJavaScript() { return enableJavaScript; }
        public void setEnableJavaScript(boolean enableJavaScript) { this.enableJavaScript = enableJavaScript; }

        public String getTrackingProtection() { return trackingProtection; }
        public void setTrackingProtection(String trackingProtection) { this.trackingProtection = trackingProtection; }

        public String getCookiePolicy() { return cookiePolicy; }
        public void setCookiePolicy(String cookiePolicy) { this.cookiePolicy = cookiePolicy; }

        public boolean isHttpsOnly() { return httpsOnly; }
        public void setHttpsOnly(boolean httpsOnly) { this.httpsOnly = httpsOnly; }

        public boolean isDoNotTrack() { return doNotTrack; }
        public void setDoNotTrack(boolean doNotTrack) { this.doNotTrack = doNotTrack; }

        @Override
        public String toString() {
            return "Privacy{" +
                    "enableJavaScript=" + enableJavaScript +
                    ", trackingProtection='" + trackingProtection + '\'' +
                    ", cookiePolicy='" + cookiePolicy + '\'' +
                    ", httpsOnly=" + httpsOnly +
                    ", doNotTrack=" + doNotTrack +
                    '}';
        }
    }

    // ---------- Performance Section ----------
    public static class Performance implements Serializable {
        private String dataSaver;
        private String preload;
        private String imageLoading;
        private String tabDiscarding;
        private boolean blockAds;

        public String getDataSaver() { return dataSaver; }
        public void setDataSaver(String dataSaver) { this.dataSaver = dataSaver; }

        public String getPreload() { return preload; }
        public void setPreload(String preload) { this.preload = preload; }

        public String getImageLoading() { return imageLoading; }
        public void setImageLoading(String imageLoading) { this.imageLoading = imageLoading; }

        public String getTabDiscarding() { return tabDiscarding; }
        public void setTabDiscarding(String tabDiscarding) { this.tabDiscarding = tabDiscarding; }

        public boolean isBlockAds() { return blockAds; }
        public void setBlockAds(boolean blockAds) { this.blockAds = blockAds; }

        @Override
        public String toString() {
            return "Performance{" +
                    "dataSaver='" + dataSaver + '\'' +
                    ", preload='" + preload + '\'' +
                    ", imageLoading='" + imageLoading + '\'' +
                    ", tabDiscarding='" + tabDiscarding + '\'' +
                    ", blockAds=" + blockAds +
                    '}';
        }
    }

    // ---------- Download Section ----------
    public static class Download implements Serializable {
        private String directory;
        private boolean wifiOnly;

        public String getDirectory() { return directory; }
        public void setDirectory(String directory) { this.directory = directory; }

        public boolean isWifiOnly() { return wifiOnly; }
        public void setWifiOnly(boolean wifiOnly) { this.wifiOnly = wifiOnly; }

        @Override
        public String toString() {
            return "Download{" +
                    "directory='" + directory + '\'' +
                    ", wifiOnly=" + wifiOnly +
                    '}';
        }
    }

    // ---------- Advanced Section ----------
    public static class Advanced implements Serializable {
        private String userAgent;
        private boolean remoteDebugging;
        private boolean crashReportingConsent;

        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

        public boolean isRemoteDebugging() { return remoteDebugging; }
        public void setRemoteDebugging(boolean remoteDebugging) { this.remoteDebugging = remoteDebugging; }

        public boolean isCrashReportingConsent() { return crashReportingConsent; }
        public void setCrashReportingConsent(boolean crashReportingConsent) { this.crashReportingConsent = crashReportingConsent; }

        @Override
        public String toString() {
            return "Advanced{" +
                    "userAgent='" + userAgent + '\'' +
                    ", remoteDebugging=" + remoteDebugging +
                    ", crashReportingConsent=" + crashReportingConsent +
                    '}';
        }
    }
}
