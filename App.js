import React, { useMemo } from 'react';
import { useColorScheme } from 'react-native';

import {
  NavigationContainer,
  DefaultTheme,
  DarkTheme,
} from '@react-navigation/native';

import { createNativeStackNavigator } from '@react-navigation/native-stack';

// Screens
import HistoryPage from './Pages/HistoryPage';
import SettingsPage from './Pages/SettingsPage';
import LibraryPage from './Pages/LibraryPage';
import BookmarkPage from './Pages/BookmarkPage';

const Stack = createNativeStackNavigator();

export default function App(props) {
  const color = useColorScheme();
  const theme = color === 'dark' ? DarkTheme : DefaultTheme;
  console.log(props)

  console.log('App initial props:', props);
  
  // ------------------------------
  // Parse shared "data" JSON
  // ------------------------------
  const parsedData = useMemo(() => {
    if (!props?.data) return [];
    try {
      const arr = JSON.parse(props.data);
      return Array.isArray(arr) ? arr : [];
    } catch {
      return [];
    }
  }, [props?.data]);

  // ------------------------------
  // Parse settings
  // ------------------------------
  const parsedSettings = useMemo(() => {
    if (!props?.settings) return {};
    try {
      return JSON.parse(props.settings);
    } catch {
      return {};
    }
  }, [props?.settings]);

  const parsedLov = useMemo(() => {
    if (!props?.lov) return {};
    try {
      return JSON.parse(props.lov);
    } catch {
      return {};
    }
  }, [props?.lov]);

  const initialRouteName = props?.initialRouteName || 'History';

  return (
    <NavigationContainer theme={theme}>
      <Stack.Navigator initialRouteName={initialRouteName}>
        {/* ---------------- History ---------------- */}
        <Stack.Screen
          name="History"
          options={{ headerShown: false }}
        >
          {() => <HistoryPage data={parsedData} />}
        </Stack.Screen>

        {/* ---------------- Library ---------------- */}
        <Stack.Screen
          name="Library"
          options={{ headerShown: false }}
        >
          {() => <LibraryPage data={parsedData} />}
        </Stack.Screen>

        {/* ---------------- Bookmarks ---------------- */}
        <Stack.Screen
          name="Bookmarks"
          options={{ headerShown: false }}
        >
          {() => <BookmarkPage data={parsedData} />}
        </Stack.Screen>

        {/* ---------------- Settings ---------------- */}
        <Stack.Screen
          name="Settings"
          options={{
            title: 'Browser Settings',
            headerStyle: { backgroundColor: '#2563eb' },
            headerTintColor: '#fff',
          }}
        >
          {() => (
            <SettingsPage
              settings={parsedSettings}
              lov={parsedLov}
            />
          )}
        </Stack.Screen>
      </Stack.Navigator>
    </NavigationContainer>
  );
}
