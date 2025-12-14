package com.mreader.LG.InitialOTO;

import com.mreader.LG.DataModel.LOVDataModel;
import com.mreader.LG.DataModel.SettingDataModel;
import com.mreader.LG.Service.LOVService;
import com.mreader.LG.Service.SettingService;

import static com.mreader.LG.DataModel.SettingDataModel.*;

import java.util.Arrays;
import java.util.List;

public class UploadDefaultConfiguration {

    private final LOVService lovService;
    private final SettingService settingService;

    private LOVDataModel lov;
    private SettingDataModel settings;

    public UploadDefaultConfiguration() {
        lovService = new LOVService();
        settingService = new SettingService();
    }

    /** Call this to (re)create defaults in DB. */
    public void restore() {
        init();
    }

    /** Internal: clears and repopulates LOV + Settings. */
    private void init() {
        // Clear old
        lovService.deleteLOV();
        settingService.deleteSetting();

        // Create & insert LOV
        lov = createDefaultLOV();
        lovService.insertLOV(lov);

        // Create & insert Settings from LOV
        settings = createDefaultSettingsFromLOV(lov);
        settingService.insertSetting(settings);
    }

    /** Build the default LOV record. */
    private LOVDataModel createDefaultLOV() {
        LOVDataModel lov = new LOVDataModel();

        lov.setHomepageLOV(Arrays.asList(
                "https://www.google.com",
                "https://www.bing.com",
                "https://duckduckgo.com",
                "https://www.yahoo.com",
                "https://startpage.com"
        ));

        lov.setSearchEngineLOV(Arrays.asList(
                "Google",
                "DuckDuckGo",
                "Bing",
                "Yahoo",
                "Ecosia",
                "Brave Search"
        ));

        lov.setLanguageLOV(Arrays.asList(
                "English (US)",
                "English (UK)",
                "Hindi",
                "Spanish",
                "French",
                "German",
                "Chinese (Simplified)",
                "Japanese"
        ));

        lov.setTrackingProtectionLOV(Arrays.asList(
                "Standard",
                "Strict",
                "Custom",
                "Off"
        ));

        lov.setCookiePolicyLOV(Arrays.asList(
                "Allow all cookies",
                "Block third-party cookies",
                "Block all cookies",
                "Ask before saving"
        ));

        lov.setDataSaverLOV(Arrays.asList(
                "Off",
                "Lite mode (compress images & scripts)",
                "Extreme mode (text only)"
        ));

        lov.setPreloadLOV(Arrays.asList(
                "Always",
                "Only on Wi-Fi",
                "Never"
        ));

        lov.setImageLoadingLOV(Arrays.asList(
                "Always load images",
                "Load only on Wi-Fi",
                "Never load images"
        ));

        lov.setTabDiscardingLOV(Arrays.asList(
                "Never discard tabs",
                "Discard after 15 minutes of inactivity",
                "Discard after 30 minutes of inactivity",
                "Discard when memory is low"
        ));

        lov.setUserAgentLOV(Arrays.asList(
                "Default (Android Browser)",
                "Desktop Mode (Chrome/Linux)",
                "Mobile Chrome",
                "Safari (iPhone)",
                "Edge",
                "Custom"
        ));

        return lov;
    }

    /** Build default settings using current LOV. */
    private SettingDataModel createDefaultSettingsFromLOV(LOVDataModel lov) {
        SettingDataModel settings = new SettingDataModel();

        // ---------- General ----------
        General general = new General();
        general.setHomepage(getFirstOrDefault(lov.getHomepageLOV(), "https://www.google.com"));
        general.setSearchEngine(getFirstOrDefault(lov.getSearchEngineLOV(), "Google"));
        general.setLanguage(getFirstOrDefault(lov.getLanguageLOV(), "English (US)"));
        general.setEnableViewMode(true);
        general.setRestoreSession(true);
        general.setTabsOpenInBackground(true);
        settings.setGeneralSection(general);

        // ---------- Privacy ----------
        Privacy privacy = new Privacy();
        privacy.setEnableJavaScript(true);
        privacy.setTrackingProtection(getFirstOrDefault(lov.getTrackingProtectionLOV(), "Standard"));
        privacy.setCookiePolicy(getFirstOrDefault(lov.getCookiePolicyLOV(), "Allow all cookies"));
        privacy.setHttpsOnly(true);
        privacy.setDoNotTrack(true);
        settings.setPrivacySection(privacy);

        // ---------- Performance ----------
        Performance perf = new Performance();
        perf.setDataSaver(getFirstOrDefault(lov.getDataSaverLOV(), "Off"));
        perf.setPreload(getFirstOrDefault(lov.getPreloadLOV(), "Always"));
        perf.setImageLoading(getFirstOrDefault(lov.getImageLoadingLOV(), "Always load images"));
        perf.setTabDiscarding(getFirstOrDefault(lov.getTabDiscardingLOV(), "Never discard tabs"));
        perf.setBlockAds(true);
        settings.setPerformanceSection(perf);

        // ---------- Download ----------
        Download download = new Download();
        download.setDirectory("/storage/emulated/0/Download/MReader");
        download.setWifiOnly(false);
        settings.setDownloadSection(download);

        // ---------- Advanced ----------
        Advanced advanced = new Advanced();
        advanced.setUserAgent(getFirstOrDefault(lov.getUserAgentLOV(), "Default (Android Browser"));
        advanced.setRemoteDebugging(false);
        advanced.setCrashReportingConsent(true);
        settings.setAdvancedSection(advanced);

        return settings;
    }

    private static String getFirstOrDefault(List<String> list, String defaultValue) {
        return (list != null && !list.isEmpty()) ? list.get(0) : defaultValue;
    }
}
