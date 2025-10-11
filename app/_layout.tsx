import { background } from "@/constants/hcColors";
import AuthProvider from "@/contexts/AuthProvider";
import SidebarProvider from "@/contexts/SidebarContext";
import BiometricAuthenticationPage from "@/pages/biometricAuth";
import NoInternetPage from "@/pages/noInternet";
import NetInfo from "@react-native-community/netinfo";
import * as DevClient from "expo-dev-client";
import * as Notifications from "expo-notifications";
import { Slot, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { KeyboardProvider } from "react-native-keyboard-controller";
import { Host } from "react-native-portalize";
import { SafeAreaView } from "react-native-safe-area-context";

export default function RootLayout() {
	const [hasInternet, setHasInternet] = useState<boolean>(false);
	const router = useRouter();

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

	useEffect(() => {
		if (__DEV__)
			DevClient.registerDevMenuItems([
				{
					name: "Go to Settings",
					callback: () => {
						router.replace("/settings");
					},
				},
			]);
	}, [router]);

	useEffect(() => {
		Notifications.setNotificationHandler({
			handleNotification: async () => ({
				shouldPlaySound: true,
				shouldSetBadge: true,
				shouldShowBanner: true,
				shouldShowList: true,
			}),
		});
	}, []);

	return (
		<SafeAreaView style={{ flex: 1, backgroundColor: background }}>
			<KeyboardProvider>
				<AuthProvider>
					<SidebarProvider>
						<Host>
							<Slot />

							<BiometricAuthenticationPage />

							{!hasInternet && <NoInternetPage />}
						</Host>
					</SidebarProvider>
				</AuthProvider>
			</KeyboardProvider>
		</SafeAreaView>
	);
}
