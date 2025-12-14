import React from 'react';
import { View, Text, Switch, StyleSheet } from 'react-native';
import { Picker } from '@react-native-picker/picker';

const AdvancedSettings = ({ data, lov, onChange }) => {
  const update = (k, v) => onChange({ ...data, [k]: v });

  return (
    <View style={styles.section}>
      <Text style={styles.heading}>Advanced</Text>

      <Text style={styles.label}>User Agent</Text>
      <Picker selectedValue={data.userAgent} onValueChange={(v) => update('userAgent', v)}>
        {lov.userAgentLOV.map((opt, idx) => (
          <Picker.Item key={idx} label={opt} value={opt} />
        ))}
      </Picker>

      <View style={styles.switchRow}>
        <Text>Enable Remote Debugging</Text>
        <Switch value={data.remoteDebugging} onValueChange={(v) => update('remoteDebugging', v)} />
      </View>

      <View style={styles.switchRow}>
        <Text>Crash Reporting Consent</Text>
        <Switch value={data.crashReportingConsent} onValueChange={(v) => update('crashReportingConsent', v)} />
      </View>
    </View>
  );
};

export default AdvancedSettings;

const styles = StyleSheet.create({
  section: { backgroundColor: '#fff', borderRadius: 12, padding: 12, marginBottom: 16 },
  heading: { fontSize: 18, fontWeight: '600', marginBottom: 8 },
  label: { fontSize: 14, fontWeight: '500', marginTop: 8 },
  switchRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginVertical: 6 },
});
