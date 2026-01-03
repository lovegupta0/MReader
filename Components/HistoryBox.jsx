import React, {useMemo} from 'react';
import {
  View,
  Text,
  StyleSheet,
  useColorScheme,
  Pressable,
  Linking,
  Image,
  NativeModules,
} from 'react-native';


const formatDate = (value) => {
  if (!value) return '';
  const d = value instanceof Date ? value : new Date(value);
  if (isNaN(d.getTime())) return '';
  return d.toLocaleString(); // e.g., "23/11/2025, 9:25 PM"
};

const normalizeUrl = (u) => {
  if (!u) return '';
  // add https if scheme missing
  return /^https?:\/\//i.test(u) ? u : `https://${u}`;
};

const getDomain = (u) => {
  try {
    const url = new URL(normalizeUrl(u));
    return url.hostname;
  } catch {
    return '';
  }
};
const {HistoryBridge} = NativeModules;
const HistoryBox = ({ url, date }) => {
  const isDark = useColorScheme() === 'dark';
  const themed = isDark ? styles.dark : styles.light;

  const displayDate = useMemo(() => formatDate(date), [date]);
  const domain = useMemo(() => getDomain(url), [url]);
  const fullUrl = useMemo(() => normalizeUrl(url), [url]);

  const openLink = async () => {
    try {
      HistoryBridge.onClickHistory(url);
    } catch {}
  };

  return (
    <Pressable
      onPress={openLink}
      android_ripple={{ color: isDark ? '#2c2c2c' : '#e6e6e6' }}
      style={[styles.box, themed.box]}
      accessibilityRole="link"
      accessibilityLabel={`Open ${domain || url}`}
    >
      <Image
        source={{ uri: `https://www.google.com/s2/favicons?domain=${domain}&sz=64` }}
        style={styles.icon}
      />
      <View style={styles.textWrap}>
        <Text
          style={[styles.url, themed.url]}
          numberOfLines={1}
          ellipsizeMode="middle"
        >
          {url}
        </Text>
        {!!displayDate && (
          <Text style={[styles.date, themed.date]}>{displayDate}</Text>
        )}
      </View>
    </Pressable>
  );
};

const styles = StyleSheet.create({
  box: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    padding: 0,
    borderRadius: 12,
    marginVertical: 2,
    borderWidth: StyleSheet.hairlineWidth,
  },
  light: {
    box: {
      backgroundColor: '#fff',
      borderColor: '#e5e7eb',
    },
    url: { color: '#111827' },
    date: { color: '#6b7280' },
  },
  dark: {
    box: {
      backgroundColor: '#151515',
      borderColor: '#2a2a2a',
    },
    url: { color: '#f3f4f6' },
    date: { color: '#9ca3af' },
  },
  icon: {
    width: 22,
    height: 22,
    borderRadius: 4,
  },
  textWrap: { flex: 1 },
  url: {
    fontSize: 14,
    fontWeight: '600',
  },
  date: {
    marginTop: 2,
    fontSize: 12,
  },
});

export default HistoryBox;
