import React, { memo, useCallback, useEffect, useMemo, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  useColorScheme,
  FlatList,
  RefreshControl,
  Pressable,
  NativeModules,
} from 'react-native';
import { SafeAreaView, useSafeAreaInsets } from 'react-native-safe-area-context';

import HistoryBox from '../Components/HistoryBox';
import SearchBox from '../Components/SearchBox';

const { HistoryBridge } = NativeModules;

const HistoryPage = ({
  data = [],
  refreshing = false,
  onRefresh,
  onClearAll,
  ListHeaderComponent,
}) => {
  const isDark = useColorScheme() === 'dark';
  const themed = isDark ? styles.dark : styles.light;

  const [localData, setLocalData] = useState(data);
  const [query, setQuery] = useState('');

  useEffect(() => setLocalData(data), [data]);

  const toDisplayDate = (value) => {
    if (!value) return '';
    const d = value instanceof Date ? value : new Date(value);
    if (isNaN(d.getTime())) return String(value);
    return d.toLocaleString();
  };

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return localData;
    return localData.filter((item) => {
      const url = (item.url ?? '').toLowerCase();
      const dateStr = toDisplayDate(item.createdOn).toLowerCase();
      return url.includes(q) || dateStr.includes(q);
    });
  }, [localData, query]);

  const renderItem = useCallback(
    ({ item }) => <HistoryBox url={item.url} date={item.createdOn}  />,
    []
  );

  const keyExtractor = useCallback((item) => String(item.id ?? item.url), []);

  const handleClearAll = useCallback(() => {
    HistoryBridge.deleteAllHistory();
    if (onClearAll) onClearAll();
    else setLocalData([]);
  }, [onClearAll]);

  return (
     <SafeAreaView
      style={{ flex: 1 }}
      edges={['top', 'bottom']}
    >
    <View style={[styles.container, themed.container]}>
      {/* Main list area */}
      <FlatList
        data={filtered}
        keyExtractor={keyExtractor}
        renderItem={renderItem}
        contentContainerStyle={[
          styles.listContent,
          filtered.length === 0 && styles.centerFill,
        ]}
        ItemSeparatorComponent={() => <View style={styles.separator} />}
        ListHeaderComponent={
          <>
            <SearchBox
              value={query}
              onChangeText={setQuery}
              onClear={() => setQuery('')}
            />
            {ListHeaderComponent ?? null}
          </>
        }
        ListEmptyComponent={
          <View style={styles.emptyWrap}>
            <Text style={[styles.emptyTitle, themed.emptyTitle]}>
              No history{query ? ' matches' : ''} yet
            </Text>
            <Text style={[styles.emptySub, themed.emptySub]}>
              {query ? 'Try a different search.' : 'Your visited links will appear here.'}
            </Text>
          </View>
        }
        refreshControl={
          onRefresh ? (
            <RefreshControl
              refreshing={refreshing}
              onRefresh={onRefresh}
              tintColor={isDark ? '#fff' : '#000'}
            />
          ) : undefined
        }
        removeClippedSubviews
        initialNumToRender={12}
        windowSize={8}
      />

      {/* Fixed footer button */}
      <View style={[styles.footer, themed.footer]}>
        <Pressable
          onPress={handleClearAll}
          style={[styles.clearAllBtn, themed.clearAllBtn]}
          android_ripple={{ color: isDark ? '#2c2c2c' : '#e6e6e6' }}
        >
          <Text style={[styles.clearAllTxt, themed.clearAllTxt]}>
            🗑️  Clear All History
          </Text>
        </Pressable>
      </View>
    </View>
    </SafeAreaView>
  );
};

export default memo(HistoryPage);

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingHorizontal: 0,
    paddingTop: 0,
  },
  light: {
    container: { backgroundColor: '#fafafa' },
    clearAllBtn: { backgroundColor: '#fff', borderColor: '#e5e7eb' },
    clearAllTxt: { color: '#dc2626' },
    emptyTitle: { color: '#111827' },
    emptySub: { color: '#6b7280' },
    footer: { backgroundColor: '#fafafa' },
  },
  dark: {
    container: { backgroundColor: '#0e0e0e' },
    clearAllBtn: { backgroundColor: '#151515', borderColor: '#2a2a2a' },
    clearAllTxt: { color: '#f87171' },
    emptyTitle: { color: '#e5e7eb' },
    emptySub: { color: '#9ca3af' },
    footer: { backgroundColor: '#0e0e0e' },
  },
  listContent: {
    paddingVertical: 0,
  },
  separator: {
    height: 0,
  },
  centerFill: {
    flexGrow: 1,
    justifyContent: 'center',
  },
  emptyWrap: {
    alignItems: 'center',
    padding: 16,
  },
  emptyTitle: {
    fontSize: 16,
    fontWeight: '700',
    marginBottom: 6,
  },
  emptySub: {
    fontSize: 13,
  },
  // Footer always visible
  footer: {
    padding: 12,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: '#3a3a3a',
  },
  clearAllBtn: {
    paddingVertical: 14,
    borderRadius: 12,
    borderWidth: StyleSheet.hairlineWidth,
    alignItems: 'center',
    justifyContent: 'center',
    width: '100%',
  },
  clearAllTxt: {
    fontSize: 16,
    fontWeight: '700',
  },
});
