import Button from "@/components/Button";
import Header from "@/components/Header";
import Sidebar from "@/components/Sidebar";
import Switch from "@/components/Switch";
import Text from "@/components/Text";
import TextInput from "@/components/TextInput";
import { red } from "@/constants/hcColors";
import { AuthContext } from "@/contexts/AuthProvider";
import { MOTIVATIONAL_QUOTES_TASK } from "@/contexts/NotificationsContext";
import * as db from "@/functions/database";
import { version, versionCode } from "@/package.json";
import { styles } from "@/styles/settings";
import * as BackgroundTask from "expo-background-task";
import * as Notifications from "expo-notifications";
import * as TaskManager from "expo-task-manager";
import { useContext, useState } from "react";
import { ScrollView, View } from "react-native";

export default function Settings() {
	const dbAPiKey = db.get("api_key");

	const [apiKey, setApiKey] = useState(dbAPiKey);

	const {
		updateAuth,
		updateBiometricsSetting,
		unlockWithBiometrics,
		supportsBiometrics,
		hasEnrolledBiometrics,
		supportsAuthenticationTypes,
	} = useContext(AuthContext);

	return (
		<View style={{ flex: 1 }}>
			<Header />
			<Sidebar />

			<ScrollView style={styles.mainContent}>
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
						onValueChange={() => {
							updateBiometricsSetting(!unlockWithBiometrics);
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
						onPress={() => {
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

					<Text style={styles.appVersionText}>
						<Text style={styles.appVersionTitle}>App Version:</Text> {version}{" "}
						<Text style={styles.appVersionBuildNumber}>({versionCode})</Text>
					</Text>
				</View>

				{__DEV__ && (
					<View style={styles.settingContainer}>
						<Text style={styles.settingTitle}>Developer Settings</Text>

						<Button
							type="primary"
							text="Test Notification Task"
							containerStyle={styles.button}
							onPress={async () => {
								await BackgroundTask.triggerTaskWorkerForTestingAsync();
							}}
						/>

						<Button
							type="primary"
							text="Register Notification Task"
							containerStyle={styles.button}
							onPress={() => {
								BackgroundTask.registerTaskAsync(MOTIVATIONAL_QUOTES_TASK);
							}}
						/>

						<Button
							type="primary"
							text="Unregister Notification Task"
							containerStyle={styles.button}
							onPress={() => {
								BackgroundTask.unregisterTaskAsync(MOTIVATIONAL_QUOTES_TASK);
							}}
						/>

						<Button
							type="primary"
							text="Send Test Notification"
							containerStyle={styles.button}
							onPress={async () => {
								Notifications.scheduleNotificationAsync({
									content: {
										title: "test notif",
									},
									trigger: null,
								});
							}}
						/>

						<Button
							type="primary"
							text="Show Bakcground Tasks"
							containerStyle={styles.button}
							onPress={async () => {
								TaskManager.getRegisteredTasksAsync().then((tasks) => {
									console.log(tasks);
								});
							}}
						/>

						<Text style={styles.appVersionText}>
							<Text style={styles.appVersionTitle}>App Version:</Text> {version}{" "}
							<Text style={styles.appVersionBuildNumber}>({versionCode})</Text>
						</Text>
					</View>
				)}
			</ScrollView>
		</View>
	);
}
