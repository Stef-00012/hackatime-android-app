import Button from "@/components/Button";
import Header from "@/components/Header";
import Sidebar from "@/components/Sidebar";
import Switch from "@/components/Switch";
import Text from "@/components/Text";
import TextInput from "@/components/TextInput";
import { red } from "@/constants/hcColors";
import { notificationCategories } from "@/constants/notifications";
import { AuthContext } from "@/contexts/AuthProvider";
import * as db from "@/functions/database";
import {
	getUserNotificationCategories,
	sendPushNotificationToken,
	updateUserNotificationCategories,
} from "@/functions/server";
import { version, versionCode } from "@/package.json";
import { styles } from "@/styles/settings";
import type { NotificationCategory } from "@/types/notifications";
import {
	Widget as TodayTimeWidget,
	handleUpdate as todayTimeWidgetHandleUpdate,
} from "@/widgets/todayTime/Widget";
import {
	Widget as TopStatsWidget,
	handleUpdate as topStatsWidgetHandleUpdate,
} from "@/widgets/topStats/Widget";
import * as Clipboard from "expo-clipboard";
import * as Notifications from "expo-notifications";
import { useContext, useEffect, useState } from "react";
import { ScrollView, ToastAndroid, View } from "react-native";
import { requestWidgetUpdate } from "react-native-android-widget";

export default function Settings() {
	const dbAPiKey = db.get("api_key");

	const [apiKey, setApiKey] = useState(dbAPiKey);

	const [notificationsPermissionStatus, setNotificationsPermissionStatus] =
		useState<Notifications.PermissionStatus>(
			Notifications.PermissionStatus.UNDETERMINED,
		);
	const [userNotificationCategories, setUserNotificationCategories] = useState<
		Record<NotificationCategory, boolean>
	>(
		notificationCategories.reduce(
			(acc, category) => {
				acc[category] = false;

				return acc;
			},
			{} as Record<NotificationCategory, boolean>,
		),
	);

	const {
		updateAuth,
		updateBiometricsSetting,
		unlockWithBiometrics,
		supportsBiometrics,
		hasEnrolledBiometrics,
		supportsAuthenticationTypes,
		requestBiometricsAuthentication,
	} = useContext(AuthContext);

	useEffect(() => {
		Notifications.getPermissionsAsync().then((status) => {
			setNotificationsPermissionStatus(status.status);
		});

		getUserNotificationCategories().then((categories) => {
			setUserNotificationCategories(categories);
		});
	}, []);

	return (
		<View style={{ flex: 1 }}>
			<Header />
			<Sidebar />

			<ScrollView>
				<View style={styles.settingContainer}>
					<Text style={styles.settingTitle}>App Settings</Text>

					<TextInput
						title="Hackatime API Key"
						password
						defaultValue={apiKey || undefined}
						placeholder="aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
						onChange={(event) => {
							setApiKey(event.nativeEvent.text);
						}}
						sideButtonContainerStyle={styles.passwordButton}
						onBeforeSideButtonPress={async (_id, isPasswordVisible) => {
							if (isPasswordVisible) return true;

							const success = await requestBiometricsAuthentication(true);

							if (!success)
								ToastAndroid.show("Authentication failed", ToastAndroid.SHORT);

							return success;
						}}
					/>

					<Switch
						title="Unlock with Biometrics"
						description={`Require biometric authentication to unlock the app${
							supportsBiometrics
								? ""
								: ".\nYour device does not have the required biometrics hardware"
						}${
							hasEnrolledBiometrics
								? ""
								: ".\nYou do not have any biometrics enrolled"
						}${
							supportsAuthenticationTypes.length <= 0
								? ".\nYour device does not support any of the required authentication types"
								: ""
						}`}
						value={unlockWithBiometrics}
						onValueChange={async () => {
							const success = await requestBiometricsAuthentication(true, true);

							if (success) updateBiometricsSetting(!unlockWithBiometrics);
						}}
						disabled={
							!supportsBiometrics ||
							!hasEnrolledBiometrics ||
							supportsAuthenticationTypes.length <= 0
						}
					/>

					<Button
						type="primary"
						text="Save"
						icon="save"
						containerStyle={styles.button}
						onPress={async () => {
							const success = await requestBiometricsAuthentication(true);

							if (!success)
								return ToastAndroid.show(
									"Authentication failed",
									ToastAndroid.SHORT,
								);

							ToastAndroid.show("Settings saved", ToastAndroid.SHORT);

							updateAuth(apiKey?.trim() || "");
						}}
					/>

					<Button
						type="outline"
						text="Logout"
						icon="logout"
						iconColor={red}
						containerStyle={styles.button}
						onPress={() => {
							updateAuth("");
						}}
					/>

					<Text style={styles.containerFooterText}>
						<Text style={styles.containerFooterTextTitle}>App Version:</Text>{" "}
						{version}{" "}
						<Text style={styles.containerFooterTextSubtitle}>
							({versionCode})
						</Text>
					</Text>
				</View>

				<View style={styles.settingContainer}>
					<Text style={styles.settingTitle}>Notifications</Text>

					<Switch
						title="Motivational Notifications"
						description={`Receive motivational notification to encourage you to code more often${
							notificationsPermissionStatus ===
							Notifications.PermissionStatus.GRANTED
								? ""
								: ".\nNotifications permission is not granted"
						}`}
						value={userNotificationCategories["motivational-quotes"]}
						onValueChange={async (toggled) => {
							setUserNotificationCategories((prev) => ({
								...prev,
								"motivational-quotes": toggled,
							}));
						}}
						disabled={
							notificationsPermissionStatus !==
							Notifications.PermissionStatus.GRANTED
						}
					/>

					{notificationsPermissionStatus ===
					Notifications.PermissionStatus.UNDETERMINED ? (
						<Button
							text="Grant Notifications Permission"
							icon="notifications"
							type="outline"
							iconColor={red}
							containerStyle={styles.button}
							onPress={async () => {
								const { status } =
									await Notifications.requestPermissionsAsync();

								if (status === Notifications.PermissionStatus.GRANTED) {
									ToastAndroid.show(
										"Notifications permission granted",
										ToastAndroid.SHORT,
									);

									const success = await sendPushNotificationToken();

									if (!success)
										ToastAndroid.show(
											"Failed to send push notification token to server",
											ToastAndroid.SHORT,
										);
								} else {
									ToastAndroid.show(
										"Notifications permission not granted",
										ToastAndroid.SHORT,
									);
								}

								setNotificationsPermissionStatus(status);
							}}
						/>
					) : notificationsPermissionStatus ===
						Notifications.PermissionStatus.DENIED ? (
						<Button
							text="Update Notifications Permission"
							icon="notifications"
							type="outline"
							iconColor={red}
							containerStyle={styles.button}
							onPress={async () => {
								const { status } = await Notifications.getPermissionsAsync();

								if (status === Notifications.PermissionStatus.GRANTED) {
									ToastAndroid.show(
										"Notifications permission granted",
										ToastAndroid.SHORT,
									);

									const success = await sendPushNotificationToken();

									if (!success)
										ToastAndroid.show(
											"Failed to send push notification token to server",
											ToastAndroid.SHORT,
										);
								} else {
									ToastAndroid.show(
										"Notifications permission not granted",
										ToastAndroid.SHORT,
									);
								}

								setNotificationsPermissionStatus(status);
							}}
						/>
					) : null}

					<Button
						type="primary"
						text="Save"
						icon="save"
						containerStyle={styles.button}
						onPress={async () => {
							const newCategories = await updateUserNotificationCategories(
								userNotificationCategories,
							);

							if (!newCategories)
								return ToastAndroid.show(
									"Failed to update notification settings",
									ToastAndroid.SHORT,
								);

							ToastAndroid.show("Settings saved", ToastAndroid.SHORT);

							setUserNotificationCategories(newCategories);
						}}
					/>

					<Text style={styles.containerFooterText}>
						<Text style={styles.containerFooterTextTitle}>
							Notification Permission:
						</Text>{" "}
						{notificationsPermissionStatus ===
						Notifications.PermissionStatus.UNDETERMINED ? (
							"Not Determined"
						) : notificationsPermissionStatus ===
							Notifications.PermissionStatus.DENIED ? (
							<Text>
								Denied{" "}
								<Text style={styles.containerFooterTextSubtitle}>
									(Please enable in system settings)
								</Text>
							</Text>
						) : (
							"Granted"
						)}
					</Text>
				</View>

				{__DEV__ && (
					<View style={styles.settingContainer}>
						<Text style={styles.settingTitle}>Developer Settings</Text>

						<Button
							type="primary"
							text="Request Biometric Authentication"
							icon="fingerprint"
							containerStyle={styles.button}
							onPress={async () => {
								const success = await requestBiometricsAuthentication(true);

								ToastAndroid.show(
									success
										? "Biometric authentication successful"
										: "Biometric authentication failed",
									ToastAndroid.SHORT,
								);
							}}
						/>

						<Button
							type="primary"
							text="Update Notifications Permission"
							icon="notifications"
							containerStyle={styles.button}
							onPress={async () => {
								const { status } = await Notifications.getPermissionsAsync();

								if (status === Notifications.PermissionStatus.GRANTED) {
									ToastAndroid.show(
										"Notifications permission granted",
										ToastAndroid.SHORT,
									);

									const success = await sendPushNotificationToken();

									if (!success)
										ToastAndroid.show(
											"Failed to send push notification token to server",
											ToastAndroid.SHORT,
										);
								} else {
									ToastAndroid.show(
										"Notifications permission not granted",
										ToastAndroid.SHORT,
									);
								}

								setNotificationsPermissionStatus(status);
							}}
						/>

						<Button
							type="primary"
							text="List Notification Channels"
							icon="notifications_active"
							containerStyle={styles.button}
							onPress={async () => {
								const channels =
									await Notifications.getNotificationChannelsAsync();

								const stringifiedChannels = JSON.stringify(channels, null, 2);

								console.debug(stringifiedChannels);

								Clipboard.setStringAsync(stringifiedChannels);

								ToastAndroid.show(
									`Copied ${channels.length} channels to clipboard and console`,
									ToastAndroid.SHORT,
								);
							}}
						/>

						<Button
							type="primary"
							text="Get Expo Push Token"
							icon="content_copy"
							containerStyle={styles.button}
							onPress={async () => {
								const expoPushToken =
									await Notifications.getExpoPushTokenAsync();

								console.debug(expoPushToken.data);

								Clipboard.setStringAsync(expoPushToken.data);

								ToastAndroid.show(
									`Copied expo push token to clipboard and console`,
									ToastAndroid.SHORT,
								);
							}}
						/>

						<Button
							type="primary"
							text="Update Today Time Widget"
							icon="refresh"
							containerStyle={styles.button}
							onPress={async () => {
								const widgetData = await todayTimeWidgetHandleUpdate();

								await requestWidgetUpdate({
									widgetName: "TodayTime",
									renderWidget: () => <TodayTimeWidget data={widgetData} />,
									widgetNotFound: () => {
										ToastAndroid.show(
											"Today Time Widget not found",
											ToastAndroid.SHORT,
										);
									},
								});

								ToastAndroid.show("Widget Updated", ToastAndroid.SHORT);
							}}
						/>

						<Button
							type="primary"
							text="Update Top Stats Widget"
							icon="refresh"
							containerStyle={styles.button}
							onPress={async () => {
								const widgetData = await topStatsWidgetHandleUpdate();

								await requestWidgetUpdate({
									widgetName: "TopStats",
									renderWidget: () => <TopStatsWidget data={widgetData} />,
									widgetNotFound: () => {
										ToastAndroid.show(
											"Today Time Widget not found",
											ToastAndroid.SHORT,
										);
									},
								});

								ToastAndroid.show("Widget Updated", ToastAndroid.SHORT);
							}}
						/>

						<Button
							type="primary"
							text="Delete Widget Preview Background"
							icon="delete"
							containerStyle={styles.button}
							onPress={async () => {
								db.del("widgetPreviewBackground");

								ToastAndroid.show(
									"Widget Background Removed",
									ToastAndroid.SHORT,
								);
							}}
						/>
					</View>
				)}
			</ScrollView>
		</View>
	);
}
