import React from 'react';
import { View, Text, StyleSheet, Switch } from 'react-native';
import { Picker } from '@react-native-picker/picker';

const GeneralSettings = ({ data, lov, onChange }) => {
  const update = (key, value) => onChange({ ...data, [key]: value });

  return (
    <View style={styles.section}>
      <Text style={styles.heading}>General</Text>

      <Text style={styles.label}>Homepage</Text>
      <Picker selectedValue={data.homepage} onValueChange={(v) => update('homepage', v)}>
        {lov.homepageLOV.map((opt, idx) => (
          <Picker.Item key={idx} label={opt} value={opt} />
        ))}
      </Picker>

      <Text style={styles.label}>Search Engine</Text>
      <Picker selectedValue={data.searchEngine} onValueChange={(v) => update('searchEngine', v)}>
        {lov.searchEngineLOV.map((opt, idx) => (
          <Picker.Item key={idx} label={opt} value={opt} />
        ))}
      </Picker>

      <Text style={styles.label}>Language</Text>
      <Picker selectedValue={data.language} onValueChange={(v) => update('language', v)}>
        {lov.languageLOV.map((opt, idx) => (
          <Picker.Item key={idx} label={opt} value={opt} />
        ))}
      </Picker>

      <View style={styles.switchRow}>
        <Text>Enable View Mode</Text>
        <Switch value={data.enableViewMode} onValueChange={(v) => update('enableViewMode', v)} />
      </View>

      <View style={styles.switchRow}>
        <Text>Restore Session</Text>
        <Switch value={data.restoreSession} onValueChange={(v) => update('restoreSession', v)} />
      </View>

      <View style={styles.switchRow}>
        <Text>Tabs Open in Background</Text>
        <Switch value={data.tabsOpenInBackground} onValueChange={(v) => update('tabsOpenInBackground', v)} />
      </View>
    </View>
  );
};

export default GeneralSettings;

const styles = StyleSheet.create({
  section: { backgroundColor: '#fff', borderRadius: 12, padding: 12, marginBottom: 16 },
  heading: { fontSize: 18, fontWeight: '600', marginBottom: 8 },
  label: { fontSize: 14, fontWeight: '500', marginTop: 8 },
  switchRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginVertical: 6 },
});
