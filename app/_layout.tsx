import Text from "@/components/Text";
import { background } from "@/constants/hcColors";
import AuthProvider from "@/contexts/AuthProvider";
import SidebarProvider from "@/contexts/SidebarContext";
import { styles } from "@/styles/noInternet";
import NetInfo from "@react-native-community/netinfo";
import { useFonts } from "expo-font";
import { Slot, SplashScreen } from "expo-router";
import { useEffect, useState } from "react";
import { View } from "react-native";
import { KeyboardProvider } from "react-native-keyboard-controller";
import { Host } from "react-native-portalize";
import { SafeAreaView } from "react-native-safe-area-context";

// import { LogBox } from 'react-native';
// LogBox.ignoreAllLogs();//Ignore all log notifications

SplashScreen.preventAutoHideAsync();

export default function RootLayout() {
	const [hasInternet, setHasInternet] = useState<boolean>(false);

	const [loaded, error] = useFonts({
		"Hackclub-Regular": require("../assets/hc_font_regular.otf"),
		MaterialSymbols: require("../assets/material-symbols.ttf"),
	});

	useEffect(() => {
		if (loaded || error) {
			SplashScreen.hideAsync();
		}

		if (error) console.error(error);
	}, [loaded, error]);

	useEffect(() => {
		NetInfo.fetch().then((state) => {
			setHasInternet((state.isConnected && state.isInternetReachable) ?? false);
		});

		const unsubscribe = NetInfo.addEventListener((state) => {
			setHasInternet((state.isConnected && state.isInternetReachable) ?? false);
		});

		return () => {
			unsubscribe();
		};
	}, []);

	if (!loaded && !error) return null;

	return (
		<SafeAreaView style={{ flex: 1, backgroundColor: background }}>
			<KeyboardProvider>
				<AuthProvider>
					<SidebarProvider>
						<Host>
							<Slot />

							{!hasInternet && (
								<View style={styles.noInternetContainer}>
									<Text style={styles.noInternetText}>
										No internet connection.
									</Text>
								</View>
							)}
						</Host>
					</SidebarProvider>
				</AuthProvider>
			</KeyboardProvider>
		</SafeAreaView>
	);
}
