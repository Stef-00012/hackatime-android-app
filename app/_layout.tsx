import Text from "@/components/Text";
import { background } from "@/constants/hcColors";
import AuthProvider from "@/contexts/AuthProvider";
import NotificationsProvider from "@/contexts/NotificationsContext";
import SidebarProvider from "@/contexts/SidebarContext";
import { styles } from "@/styles/noInternet";
import NetInfo from "@react-native-community/netinfo";
import { Slot } from "expo-router";
import { useEffect, useState } from "react";
import { View } from "react-native";
import { KeyboardProvider } from "react-native-keyboard-controller";
import { Host } from "react-native-portalize";
import { SafeAreaView } from "react-native-safe-area-context";

export default function RootLayout() {
	const [hasInternet, setHasInternet] = useState<boolean>(false);

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

	return (
		<SafeAreaView style={{ flex: 1, backgroundColor: background }}>
			<NotificationsProvider>
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
			</NotificationsProvider>
		</SafeAreaView>
	);
}
