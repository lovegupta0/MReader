// src/components/SearchBox.js
import React, { memo } from 'react';
import {
  View,
  Text,
  TextInput,
  Pressable,
  StyleSheet,
  useColorScheme,
} from 'react-native';

const SearchBox = ({
  value,
  onChangeText,
  placeholder = 'Search history (URL or date)…',
  onClear,
  style,
  inputProps = {},
}) => {
  const isDark = useColorScheme() === 'dark';
  const themed = isDark ? styles.dark : styles.light;

  return (
    <View style={[styles.wrap, themed.wrap, style]}>
      <Text style={[styles.icon, themed.icon]}>🔎</Text>

      <TextInput
        value={value}
        onChangeText={onChangeText}
        placeholder={placeholder}
        placeholderTextColor={isDark ? '#7a7f87' : '#9aa0a6'}
        style={[styles.input, themed.input]}
        autoCapitalize="none"
        autoCorrect={false}
        clearButtonMode="while-editing"
        returnKeyType="search"
        {...inputProps}
      />

      {value?.length > 0 && (
        <Pressable
          onPress={onClear}
          hitSlop={8}
          style={styles.clearBtn}
          android_ripple={{ color: isDark ? '#2c2c2c' : '#e6e6e6', borderless: true }}
          accessibilityRole="button"
          accessibilityLabel="Clear search"
        >
          <Text style={[styles.clearTxt, themed.clearTxt]}>✕</Text>
        </Pressable>
      )}
    </View>
  );
};

export default memo(SearchBox);

const styles = StyleSheet.create({
  wrap: {
    flexDirection: 'row',
    alignItems: 'center',
    borderRadius: 12,
    borderWidth: StyleSheet.hairlineWidth,
    paddingHorizontal: 12,
    paddingVertical: 8,
    marginBottom: 10,
  },
  light: {
    wrap: { backgroundColor: '#fff', borderColor: '#e5e7eb' },
    input: { color: '#111827' },
    icon: { color: '#6b7280' },
    clearTxt: { color: '#6b7280' },
  },
  dark: {
    wrap: { backgroundColor: '#151515', borderColor: '#2a2a2a' },
    input: { color: '#f3f4f6' },
    icon: { color: '#9ca3af' },
    clearTxt: { color: '#9ca3af' },
  },
  icon: { fontSize: 16, marginRight: 8 },
  input: { flex: 1, fontSize: 15, paddingVertical: 0 },
  clearBtn: { paddingHorizontal: 4, paddingVertical: 2 },
  clearTxt: { fontSize: 16 },
});
