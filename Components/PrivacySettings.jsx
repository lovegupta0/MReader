import React from 'react';
import { View, Text, Switch, StyleSheet } from 'react-native';
import { Picker } from '@react-native-picker/picker';

const PrivacySettings = ({ data, lov, onChange }) => {
  const update = (k, v) => onChange({ ...data, [k]: v });

  return (
    <View style={styles.section}>
      <Text style={styles.heading}>Privacy</Text>

      <View style={styles.switchRow}>
        <Text>Enable JavaScript</Text>
        <Switch value={data.enableJavaScript} onValueChange={(v) => update('enableJavaScript', v)} />
      </View>

      <Text style={styles.label}>Tracking Protection</Text>
      <Picker selectedValue={data.trackingProtection} onValueChange={(v) => update('trackingProtection', v)}>
        {lov.trackingProtectionLOV.map((opt, idx) => (
          <Picker.Item key={idx} label={opt} value={opt} />
        ))}
      </Picker>

      <Text style={styles.label}>Cookie Policy</Text>
      <Picker selectedValue={data.cookiePolicy} onValueChange={(v) => update('cookiePolicy', v)}>
        {lov.cookiePolicyLOV.map((opt, idx) => (
          <Picker.Item key={idx} label={opt} value={opt} />
        ))}
      </Picker>

      <View style={styles.switchRow}>
        <Text>HTTPS Only</Text>
        <Switch value={data.httpsOnly} onValueChange={(v) => update('httpsOnly', v)} />
      </View>

      <View style={styles.switchRow}>
        <Text>Do Not Track</Text>
        <Switch value={data.doNotTrack} onValueChange={(v) => update('doNotTrack', v)} />
      </View>
    </View>
  );
};

export default PrivacySettings;

const styles = StyleSheet.create({
  section: { backgroundColor: '#fff', borderRadius: 12, padding: 12, marginBottom: 16 },
  heading: { fontSize: 18, fontWeight: '600', marginBottom: 8 },
  label: { fontSize: 14, fontWeight: '500', marginTop: 8 },
  switchRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginVertical: 6 },
});
