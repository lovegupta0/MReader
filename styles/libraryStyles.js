import { StyleSheet } from 'react-native';

export const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  list: {
    padding: 12,
  },
  card: {
    flexDirection: 'row',
    borderRadius: 14,
    padding: 10,
    marginBottom: 12,
    elevation: 2,
  },
  cover: {
    width: 70,
    height: 90,
    borderRadius: 8,
    backgroundColor: '#ddd',
  },
  info: {
    flex: 1,
    marginLeft: 12,
  },
  titleRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  title: {
    flex: 1,
    fontSize: 16,
    fontWeight: '700',
  },
  newBadge: {
    backgroundColor: '#ef4444',
    color: '#fff',
    fontSize: 11,
    paddingHorizontal: 8,
    paddingVertical: 2,
    borderRadius: 10,
    marginLeft: 6,
    overflow: 'hidden',
  },
  subText: {
    fontSize: 13,
    marginTop: 4,
  },
  sourceRow: {
    marginTop: 6,
  },
  sourceBadge: {
    backgroundColor: '#e5e7eb',
    alignSelf: 'flex-start',
    fontSize: 11,
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 6,
    color: '#374151',
  },
});

export const lightTheme = {
  container: { backgroundColor: '#f9fafb' },
  card: { backgroundColor: '#ffffff' },
  title: { color: '#111827' },
  subText: { color: '#6b7280' },
};

export const darkTheme = {
  container: { backgroundColor: '#0e0e0e' },
  card: { backgroundColor: '#151515' },
  title: { color: '#f9fafb' },
  subText: { color: '#9ca3af' },
};
