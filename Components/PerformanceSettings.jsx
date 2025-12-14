import React from 'react';
import { View, Text, Switch, StyleSheet } from 'react-native';
import { Picker } from '@react-native-picker/picker';

const PerformanceSettings = ({ data, lov, onChange }) => {
  const update = (k, v) => onChange({ ...data, [k]: v });

  return (
    <View style={styles.section}>
      <Text style={styles.heading}>Performance</Text>

      <Text style={styles.label}>Data Saver</Text>
      <Picker selectedValue={data.dataSaver} onValueChange={(v) => update('dataSaver', v)}>
        {lov.dataSaverLOV.map((opt, idx) => (
          <Picker.Item key={idx} label={opt} value={opt} />
        ))}
      </Picker>

      <Text style={styles.label}>Preload</Text>
      <Picker selectedValue={data.preload} onValueChange={(v) => update('preload', v)}>
        {lov.preloadLOV.map((opt, idx) => (
          <Picker.Item key={idx} label={opt} value={opt} />
        ))}
      </Picker>

      <Text style={styles.label}>Image Loading</Text>
      <Picker selectedValue={data.imageLoading} onValueChange={(v) => update('imageLoading', v)}>
        {lov.imageLoadingLOV.map((opt, idx) => (
          <Picker.Item key={idx} label={opt} value={opt} />
        ))}
      </Picker>

      <Text style={styles.label}>Tab Discarding</Text>
      <Picker selectedValue={data.tabDiscarding} onValueChange={(v) => update('tabDiscarding', v)}>
        {lov.tabDiscardingLOV.map((opt, idx) => (
          <Picker.Item key={idx} label={opt} value={opt} />
        ))}
      </Picker>

      <View style={styles.switchRow}>
        <Text>Block Ads</Text>
        <Switch value={data.blockAds} onValueChange={(v) => update('blockAds', v)} />
      </View>
    </View>
  );
};

export default PerformanceSettings;

const styles = StyleSheet.create({
  section: { backgroundColor: '#fff', borderRadius: 12, padding: 12, marginBottom: 16 },
  heading: { fontSize: 18, fontWeight: '600', marginBottom: 8 },
  label: { fontSize: 14, fontWeight: '500', marginTop: 8 },
  switchRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginVertical: 6 },
});
