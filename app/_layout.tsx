import { background } from "@/constants/hcColors";
import AuthProvider from "@/contexts/AuthProvider";
import { styles } from "@/styles/noInternet";
import NetInfo from "@react-native-community/netinfo";
import { useFonts } from "expo-font";
import { Slot, SplashScreen } from "expo-router";
import React, { useEffect, useState } from "react";
import { Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

SplashScreen.preventAutoHideAsync();

export default function RootLayout() {
    const [hasInternet, setHasInternet] = useState<boolean>(false);

    const [loaded, error] = useFonts({
        "Hackclub-Regular": require("../assets/hc_font_regular.otf")
    })

    useEffect(() => {
        if (loaded || error) {
            SplashScreen.hideAsync();
        }

        if (error) console.error(error)
    }, [loaded, error]);

    useEffect(() => {
        NetInfo.fetch().then(state => {
            setHasInternet((state.isConnected && state.isInternetReachable) ?? false)
        })

        const unsubscribe = NetInfo.addEventListener((state) => {
            setHasInternet((state.isConnected && state.isInternetReachable) ?? false)
        })

        return () => {
            unsubscribe();
        }
    }, [])

    if (!loaded && !error) return null;

	return (
        <SafeAreaView style={{ flex: 1, backgroundColor: background }}>
            <AuthProvider>
                <Slot />

                {!hasInternet && (
                    <View style={styles.noInternetContainer}>
                        <Text style={styles.noInternetText}>
                            No internet connection.
                        </Text>
                    </View>
                )}
            </AuthProvider>
        </SafeAreaView>
	);
}
