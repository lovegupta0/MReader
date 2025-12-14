import React, { useMemo } from 'react';
import { NavigationContainer, DefaultTheme, DarkTheme } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { useColorScheme } from 'react-native';
import HistoryPage from './Pages/HistoryPage';
import SettingsPage from './Pages/SettingsPage';

const Stack = createNativeStackNavigator();

/**
 * App receives initial props from native via ReactActivityDelegate.getLaunchOptions():
 *  - initialRouteName?: string  (e.g., "History")
 *  - initialData?: string (JSON array)  -> used as route param for History
 */
export default function App(props) {
  const color = useColorScheme();
  const theme = color === 'dark' ? DarkTheme : DefaultTheme;
  console.log('App props:', props);

  // ✅ Parse History Data (if sent from Android)
  const parsedData = useMemo(() => {
    if (!props?.data) return undefined;
    try {
      const arr = JSON.parse(props.data);
      return Array.isArray(arr) ? arr : undefined;
    } catch {
      return undefined;
    }
  }, [props?.data]);

  // ✅ Parse Settings + LOV data (if sent from Android)
  const parsedSettings = useMemo(() => {
    if (!props?.settings) return undefined;
    try {
      return JSON.parse(props.settings);
    } catch {
      return undefined;
    }
  }, [props?.settings]);

  const parsedLov = useMemo(() => {
    if (!props?.lov) return undefined;
    try {
      return JSON.parse(props.lov);
    } catch {
      return undefined;
    }
  }, [props?.lov]);

  // ✅ Determine initial route (default to History)
  const initialRouteName = props?.initialRouteName || 'History';

  const initialParams =
    initialRouteName === 'History' && parsedData
      ? { data: parsedData }
      : initialRouteName === 'Settings' && parsedSettings
      ? { settings: parsedSettings, lov: parsedLov }
      : undefined;

  return (
    <NavigationContainer theme={theme}>
      <Stack.Navigator initialRouteName={initialRouteName}>
        {/* History Screen */}
        <Stack.Screen
          name="History"
          options={{ headerShown: false }}
          initialParams={initialParams}
        >
          {({ route }) => <HistoryPage data={route.params?.data ?? []} />}
        </Stack.Screen>

        {/* ✅ Settings Screen */}
        <Stack.Screen
          name="Settings"
          options={{
            title: 'Browser Settings',
            headerStyle: { backgroundColor: '#2563eb' },
            headerTintColor: '#fff',
            headerTitleStyle: { fontWeight: '600' },
          }}
          initialParams={initialParams}
        >
          {({ route }) => (
            <SettingsPage
              settings={route.params?.settings ?? {}}
              lov={route.params?.lov ?? {}}
            />
          )}
        </Stack.Screen>
      </Stack.Navigator>
    </NavigationContainer>
  );
}