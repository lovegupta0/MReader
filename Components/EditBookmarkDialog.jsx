import React, { useEffect, useState, memo } from 'react';
import {
  Modal,
  View,
  Text,
  TextInput,
  Pressable,
  StyleSheet,
  useColorScheme,
} from 'react-native';

const EditBookmarkDialog = ({
  visible,
  bookmark,
  onCancel,
  onSave,
}) => {
  const isDark = useColorScheme() === 'dark';
  const themed = isDark ? styles.dark : styles.light;

  const [title, setTitle] = useState('');
  const [address, setAddress] = useState('');

  useEffect(() => {
    if (bookmark) {
      setTitle(bookmark.title || '');
      setAddress(bookmark.address || '');
    }
  }, [bookmark]);

  const handleSave = () => {
    if (!address.trim()) return;

    onSave({
      ...bookmark,
      title: title.trim() || 'Untitled',
      address: address.trim(),
    });
  };

  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={onCancel}
    >
      <View style={styles.backdrop}>
        <View style={[styles.card, themed.card]}>
          <Text style={[styles.header, themed.header]}>
            Edit Bookmark
          </Text>

          <TextInput
            value={title}
            onChangeText={setTitle}
            placeholder="Title"
            placeholderTextColor={isDark ? '#9ca3af' : '#6b7280'}
            style={[styles.input, themed.input]}
          />

          <TextInput
            value={address}
            onChangeText={setAddress}
            placeholder="Address / URL"
            placeholderTextColor={isDark ? '#9ca3af' : '#6b7280'}
            style={[styles.input, themed.input]}
            autoCapitalize="none"
            autoCorrect={false}
          />

          <View style={styles.actions}>
            <Pressable onPress={onCancel} style={styles.btn}>
              <Text style={[styles.btnText, themed.cancel]}>
                Cancel
              </Text>
            </Pressable>

            <Pressable onPress={handleSave} style={styles.btn}>
              <Text style={[styles.btnText, themed.save]}>
                Save
              </Text>
            </Pressable>
          </View>
        </View>
      </View>
    </Modal>
  );
};

export default memo(EditBookmarkDialog);

const styles = StyleSheet.create({
  backdrop: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.45)',
    justifyContent: 'center',
    padding: 24,
  },
  card: {
    borderRadius: 14,
    padding: 16,
  },
  header: {
    fontSize: 16,
    fontWeight: '700',
    marginBottom: 12,
  },
  input: {
    borderWidth: StyleSheet.hairlineWidth,
    borderRadius: 10,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 15,
    marginBottom: 10,
  },
  actions: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
  },
  btn: {
    paddingHorizontal: 12,
    paddingVertical: 8,
  },
  btnText: {
    fontSize: 15,
    fontWeight: '600',
  },

  /* Light */
  light: {
    card: { backgroundColor: '#ffffff' },
    header: { color: '#111827' },
    input: { borderColor: '#e5e7eb', color: '#111827' },
    cancel: { color: '#6b7280' },
    save: { color: '#2563eb' },
  },

  /* Dark */
  dark: {
    card: { backgroundColor: '#151515' },
    header: { color: '#f3f4f6' },
    input: { borderColor: '#2a2a2a', color: '#f3f4f6' },
    cancel: { color: '#9ca3af' },
    save: { color: '#60a5fa' },
  },
});
