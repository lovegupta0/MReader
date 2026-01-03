import React, { useState } from 'react';
import {
  View,
  TextInput,
  Pressable,
  StyleSheet,
} from 'react-native';

export default function LibraryHeader({
  onSearch,
  onOpenMenu,
}) {
  const [query, setQuery] = useState('');

  const handleChange = (text) => {
    setQuery(text);
    onSearch(text);
  };

  return (
    <View style={styles.header}>
      <TextInput
        placeholder="Search library…"
        value={query}
        onChangeText={handleChange}
        style={styles.search}
      />

      <Pressable onPress={onOpenMenu} style={styles.menuBtn}>
        <View style={styles.dot} />
        <View style={styles.dot} />
        <View style={styles.dot} />
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  header: {
    flexDirection: 'row',
    padding: 12,
    alignItems: 'center',
  },
  search: {
    flex: 1,
    height: 40,
    backgroundColor: '#f1f5f9',
    borderRadius: 10,
    paddingHorizontal: 12,
  },
  menuBtn: {
    marginLeft: 12,
    justifyContent: 'center',
  },
  dot: {
    width: 4,
    height: 4,
    borderRadius: 2,
    backgroundColor: '#374151',
    marginVertical: 2,
  },
});
