import React, { useState, useMemo } from 'react';
import {
  View,
  FlatList,
  Alert,
  ActionSheetIOS,
  Platform,
} from 'react-native';

import LibraryHeader from '../Components/LibraryHeader';
import LibraryCard from '../Components/LibraryCard';
import { styles, lightTheme, darkTheme } from '../styles/libraryStyles';
import { useColorScheme } from 'react-native';

export default function LibraryPage({ data = [] }) {
  const isDark = useColorScheme() === 'dark';
  const theme = isDark ? darkTheme : lightTheme;

  const [query, setQuery] = useState('');
  const [items, setItems] = useState(data);

  // 🔍 Search filter
  const filtered = useMemo(() => {
    if (!query) return items;
    return items.filter((i) =>
      i.title.toLowerCase().includes(query.toLowerCase())
    );
  }, [query, items]);

  // ⋮ Menu
  const openMenu = () => {
    const options = [
      'Sort A–Z',
      'Sort Z–A',
      'Recently Updated',
      'Delete All',
      'Cancel',
    ];

    const cancelButtonIndex = 4;

    if (Platform.OS === 'ios') {
      ActionSheetIOS.showActionSheetWithOptions(
        { options, cancelButtonIndex },
        handleMenuAction
      );
    } else {
      Alert.alert(
        'Options',
        '',
        options.map((o, i) => ({
          text: o,
          onPress: () => handleMenuAction(i),
          style: o === 'Delete All' ? 'destructive' : 'default',
        }))
      );
    }
  };

  const handleMenuAction = (index) => {
    switch (index) {
      case 0:
        setItems([...items].sort((a, b) => a.title.localeCompare(b.title)));
        break;
      case 1:
        setItems([...items].sort((a, b) => b.title.localeCompare(a.title)));
        break;
      case 2:
        setItems(
          [...items].sort(
            (a, b) =>
              new Date(b.lastUpdateddate) -
              new Date(a.lastUpdateddate)
          )
        );
        break;
      case 3:
        Alert.alert(
          'Delete all?',
          'This cannot be undone.',
          [
            { text: 'Cancel' },
            {
              text: 'Delete',
              style: 'destructive',
              onPress: () => setItems([]),
            },
          ]
        );
        break;
    }
  };

  return (
    <View style={[styles.container, theme.container]}>
      <LibraryHeader
        onSearch={setQuery}
        onOpenMenu={openMenu}
      />

      <FlatList
        data={filtered}
        keyExtractor={(item) => String(item.id)}
        renderItem={({ item }) => (
          <LibraryCard item={item} theme={theme} />
        )}
      />
    </View>
  );
}
