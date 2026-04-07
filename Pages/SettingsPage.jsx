import React, { useState } from 'react';
import {
  ScrollView,
  View,
  Text,
  StyleSheet,
  Pressable,
  Alert,
  Platform,
  NativeModules,
} from 'react-native';
import { SafeAreaView, useSafeAreaInsets } from 'react-native-safe-area-context';

import GeneralSettings from '../Components/GeneralSettings';
import PrivacySettings from '../Components/PrivacySettings';
import PerformanceSettings from '../Components/PerformanceSettings';
import DownloadSettings from '../Components/DownloadSettings';
import AdvancedSettings from '../Components/AdvancedSettings';

const { SettingsBridge } = NativeModules; // Native module bridge (Android)

const SettingsPage = ({ settings, lov }) => {
  const [data, setData] = useState(settings);
  const [isSaving, setIsSaving] = useState(false);

  const handleSectionChange = (section, updatedSection) => {
    setData((prev) => ({ ...prev, [section]: updatedSection }));
  };

  const handleSave = async () => {
    try {
      setIsSaving(true);
      const payload = JSON.stringify(data);

      if (Platform.OS === 'android' && SettingsBridge?.saveSettings) {
        await SettingsBridge.saveSettings(payload);
      }

      Alert.alert('✅ Settings Saved', 'Your preferences have been updated.');
    } catch (error) {
      console.error('Save failed:', error);
      Alert.alert('❌ Error', 'Unable to save settings.');
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <SafeAreaView style={{ flex: 1 }} edges={['top', 'bottom']}>
    <View style={styles.wrapper}>
      <ScrollView style={styles.container}>
        

        <GeneralSettings
          data={data.generalSection}
          lov={lov}
          onChange={(v) => handleSectionChange('generalSection', v)}
        />

        <PrivacySettings
          data={data.privacySection}
          lov={lov}
          onChange={(v) => handleSectionChange('privacySection', v)}
        />

        <PerformanceSettings
          data={data.performanceSection}
          lov={lov}
          onChange={(v) => handleSectionChange('performanceSection', v)}
        />

        <DownloadSettings
          data={data.downloadSection}
          onChange={(v) => handleSectionChange('downloadSection', v)}
        />

        <AdvancedSettings
          data={data.advancedSection}
          lov={lov}
          onChange={(v) => handleSectionChange('advancedSection', v)}
        />

        <View style={{ height: 100 }} />
      </ScrollView>

      <View style={styles.footer}>
        <Pressable
          onPress={handleSave}
          disabled={isSaving}
          style={({ pressed }) => [
            styles.saveButton,
            pressed && { opacity: 0.8 },
          ]}>
          <Text style={styles.saveText}>
            {isSaving ? 'Saving...' : '💾 Save Changes'}
          </Text>
        </Pressable>
      </View>
    </View>
    </SafeAreaView>
  );
};

export default SettingsPage;

const styles = StyleSheet.create({
  wrapper: { flex: 1, backgroundColor: '#fafafa' },
  container: { flex: 1, padding: 12 },
  title: { fontSize: 20, fontWeight: '700', marginBottom: 16 },

  footer: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: '#ffffff',
    paddingVertical: 14,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: '#ddd',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: -1 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 4,
  },
  saveButton: {
    backgroundColor: '#2563eb',
    borderRadius: 12,
    marginHorizontal: 16,
    paddingVertical: 14,
    alignItems: 'center',
  },
  saveText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
});
