import React, { useMemo, useState } from 'react';
import {
  View,
  Text,
  FlatList,
  Pressable,
  Alert,
  StyleSheet,
  useColorScheme,
  NativeModules

} from 'react-native';
import { SafeAreaView, useSafeAreaInsets } from 'react-native-safe-area-context';
import SearchBox from '../Components/SearchBox';
import EditBookmarkDialog from '../Components/EditBookmarkDialog';
const { BookmarksBridge } = NativeModules;

export default function BookmarkPage({ data = [] }) {
  const isDark = useColorScheme() === 'dark';
  const themed = isDark ? styles.dark : styles.light;

  const [query, setQuery] = useState('');
  const [items, setItems] = useState(data);
  const [editing, setEditing] = useState(null);

  // 🔍 Search filter
  const filtered = useMemo(() => {
    if (!query) return items;
    const q = query.toLowerCase();
    return items.filter(
      (b) =>
        b.title?.toLowerCase().includes(q) ||
        b.address?.toLowerCase().includes(q)
    );
  }, [query, items]);

  const onOpen = (item) => {
    console.log('Open bookmark:', item.address);
    BookmarksBridge.OnClickBookmark(item.address);
  };

  const onLongPress = (item) => {
    Alert.alert(
      item.title || 'Bookmark',
      '',
      [
        { text: 'Open', onPress: () => onOpen(item) },
        { text: 'Edit', onPress: () => setEditing(item) },
        {
          text: 'Delete',
          style: 'destructive',
          onPress: () =>{
            BookmarksBridge.onClickeDelete(JSON.stringify(item));
            setItems((prev) =>
              prev.filter((b) => b.id !== item.id)
            );
          },
        },
        { text: 'Cancel' },
      ]
    );
  };

  const onSaveEdit = (updated) => {
    setItems((prev) =>
      prev.map((b) => (b.id === updated.id ? updated : b))
    );
    BookmarksBridge.updateBookmark(JSON.stringify(updated));
    setEditing(null);
  };

  const renderItem = ({ item }) => (
    <Pressable
      onPress={() => onOpen(item)}
      onLongPress={() => onLongPress(item)}
      style={[styles.card, themed.card]}
    >
      <Text style={[styles.title, themed.title]} numberOfLines={1}>
        {item.title || 'Untitled'}
      </Text>

      <Text
        style={[styles.address, themed.address]}
        numberOfLines={1}
      >
        {item.address}
      </Text>
    </Pressable>
  );

  return (
    <SafeAreaView style={{ flex: 1 }} edges={['top', 'bottom']}>
    <View style={[styles.container, themed.container]}>
      <SearchBox
        value={query}
        onChangeText={setQuery}
        onClear={() => setQuery('')}
        placeholder="Search bookmarks…"
      />

      <FlatList
        data={filtered}
        keyExtractor={(item) => String(item.id)}
        renderItem={renderItem}
        ItemSeparatorComponent={() => (
          <View style={styles.separator} />
        )}
        ListEmptyComponent={
          <Text style={[styles.empty, themed.empty]}>
            No bookmarks found
          </Text>
        }
      />

      {/* ✏️ Edit Dialog */}
      <EditBookmarkDialog
        visible={!!editing}
        bookmark={editing}
        onCancel={() => setEditing(null)}
        onSave={onSaveEdit}
      />
    </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 12,
  },

  card: {
    paddingVertical: 10,
    paddingHorizontal: 12,
    borderRadius: 10,
  },

  title: {
    fontSize: 15,
    fontWeight: '600',
  },

  address: {
    fontSize: 13,
    marginTop: 2,
  },

  separator: {
    height: StyleSheet.hairlineWidth,
    marginVertical: 8,
  },

  empty: {
    textAlign: 'center',
    marginTop: 40,
    fontSize: 14,
  },

  /* Light */
  light: {
    container: { backgroundColor: '#fafafa' },
    card: { backgroundColor: '#ffffff' },
    title: { color: '#111827' },
    address: { color: '#6b7280' },
    empty: { color: '#6b7280' },
  },

  /* Dark */
  dark: {
    container: { backgroundColor: '#0e0e0e' },
    card: { backgroundColor: '#151515' },
    title: { color: '#f3f4f6' },
    address: { color: '#9ca3af' },
    empty: { color: '#9ca3af' },
  },
});
