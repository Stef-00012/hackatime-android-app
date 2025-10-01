import Button from "@/components/Button";
import Header from "@/components/Header";
import Sidebar from "@/components/Sidebar";
import Switch from "@/components/Switch";
import Text from "@/components/Text";
import TextInput from "@/components/TextInput";
import { red } from "@/constants/hcColors";
import { AuthContext } from "@/contexts/AuthProvider";
import * as db from "@/functions/database";
import { version, versionCode } from "@/package.json";
import { styles } from "@/styles/settings";
import { useContext, useState } from "react";
import { ScrollView, ToastAndroid, View } from "react-native";

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
		requestBiometricsAuthentication,
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
					</View>
				)}
			</ScrollView>
		</View>
	);
}
