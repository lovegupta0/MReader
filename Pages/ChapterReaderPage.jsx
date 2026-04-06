import React, { useRef } from "react";
import {
  View,
  Text,
  FlatList,
  Image,
  StyleSheet,
  NativeModules,
  Dimensions
} from "react-native";

/**
 * Native bridge exposed from Java
 */
const { ChapterReaderBridge } = NativeModules;

/* ===================== TYPES ===================== */



/* ===================== COMPONENT ===================== */

export default function ChapterReaderPage({ chapter }) {
  const triggeredRef = useRef(false);

  /**
   * 🔔 Notify Java to load next chapter
   */
  const notifyJavaLoadNext = () => {
    if (triggeredRef.current) return;
    triggeredRef.current = true;

    if (ChapterReaderBridge?.onRequestNextChapter) {
      ChapterReaderBridge.onRequestNextChapter();
    }
  };

  /**
   * 🔍 Detect 2nd last visible page
   */
  const onViewableItemsChanged = useRef(
    ({ viewableItems }) => {
      if (!chapter?.pages?.length) return;

      const secondLastIndex = chapter.pages.length - 2;

      viewableItems.forEach((item) => {
        if (item.index === secondLastIndex) {
          notifyJavaLoadNext();
        }
      });
    }
  ).current;

  /**
   * Reset trigger when Java sends updated chapter
   */
  if (triggeredRef.current && chapter?.pages?.length) {
    triggeredRef.current = false;
  }

  if (!chapter) {
    return (
      <View style={styles.center}>
        <Text>Loading…</Text>
      </View>
    );
  }

  return (
    <View style={styles.root}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.title}>{chapter.title}</Text>
      </View>

      {/* Long strip reader */}
      <FlatList
        data={chapter.pages}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <Image
            source={{ uri: item.optimizedUri || item.sourceUri }}
            style={styles.pageImage}
            resizeMode="contain"
          />
        )}
        onViewableItemsChanged={onViewableItemsChanged}
        viewabilityConfig={{
          itemVisiblePercentThreshold: 20
        }}
        removeClippedSubviews
        initialNumToRender={3}
        windowSize={5}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: "#000"
  },

  header: {
    padding: 10,
    backgroundColor: "#111",
    alignItems: "center"
  },

  title: {
    color: "#fff",
    fontSize: 14,
    fontWeight: "bold"
  },

  pageImage: {
    width: Dimensions.get("window").width,
    height: Dimensions.get("window").width * 1.4,
    marginBottom: 8
  },

  center: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center"
  }
});

