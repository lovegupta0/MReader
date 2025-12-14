import React from 'react';
import { View, Text, TextInput, Switch, StyleSheet } from 'react-native';

const DownloadSettings = ({ data, onChange }) => {
  const update = (k, v) => onChange({ ...data, [k]: v });

  return (
    <View style={styles.section}>
      <Text style={styles.heading}>Download</Text>

      <Text style={styles.label}>Download Directory</Text>
      <TextInput
        style={styles.input}
        value={data.directory}
        onChangeText={(v) => update('directory', v)}
        placeholder="/storage/emulated/0/Download"
      />

      <View style={styles.switchRow}>
        <Text>Wi-Fi Only</Text>
        <Switch value={data.wifiOnly} onValueChange={(v) => update('wifiOnly', v)} />
      </View>
    </View>
  );
};

export default DownloadSettings;

const styles = StyleSheet.create({
  section: { backgroundColor: '#fff', borderRadius: 12, padding: 12, marginBottom: 16 },
  heading: { fontSize: 18, fontWeight: '600', marginBottom: 8 },
  label: { fontSize: 14, fontWeight: '500', marginTop: 8 },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    padding: 8,
    marginTop: 6,
  },
  switchRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginVertical: 6 },
});
