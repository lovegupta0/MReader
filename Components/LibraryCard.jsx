import React from 'react';
import {
  View,
  Text,
  Image,
  Pressable,
  Alert,
  NativeModules,
} from 'react-native';

import { styles } from '../styles/libraryStyles';
import { isRecent, getCoverUrl, getSource } from '../utils/libraryUtils';

const { LibraryBridge } = NativeModules;

export default function LibraryCard({ item, theme }) {
  const isNew = isRecent(item.lastUpdateddate);
  console.log('Cover URL:'+ item.coverUrl);
  const onPress = () => {
    LibraryBridge.onLibraryItemClick(JSON.stringify(item));
  };

  const onLongPress = () => {
    Alert.alert(
      item.title,
      '',
      [
        {
          text: 'Open',
          onPress: onPress,
        },
        {
          text: 'Delete',
          style: 'destructive',
          onPress: () =>
            LibraryBridge.onLibraryItemDelete(
              JSON.stringify(item)
            ),
        },
        { text: 'Cancel' },
      ]
    );
  };

  return (
    <Pressable onPress={onPress} onLongPress={onLongPress}>
      <View style={[styles.card, theme.card]}>
        <Image source={{ uri: item.coverUrl }} style={styles.cover} />

        <View style={styles.info}>
          <View style={styles.titleRow}>
            <Text style={[styles.title, theme.title]} numberOfLines={2}>
              {item.title}
            </Text>
            {isNew && <Text style={styles.newBadge}>NEW</Text>}
          </View>

          <Text style={[styles.subText, theme.subText]}>
            Latest Chapter: {item.latestchapter}
          </Text>

          <Text style={[styles.subText, theme.subText]}>
            Updated: {item.latestChapterUpdated}
          </Text>

          <Text style={styles.sourceBadge}>
            {getSource(item.baseUrl)}
          </Text>
        </View>
      </View>
    </Pressable>
  );
}
