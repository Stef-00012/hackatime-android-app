import Button from "@/components/Button";
import Switch from "@/components/Switch";
import Text from "@/components/Text";
import TextInput from "@/components/TextInput";
import { AuthContext } from "@/contexts/AuthProvider";
import * as db from "@/functions/database";
import { styles } from "@/styles/login";
import { Link } from "expo-router";
import { useContext, useEffect, useState } from "react";
import { View } from "react-native";
import { KeyboardAvoidingView } from "react-native-keyboard-controller";

const apiRegex =
	/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

export default function Login() {
	const [error, setError] = useState<string | null>(null);
	const [apiKey, setApiKey] = useState<string>("");
	const [shareAPIKey, setShareAPIKey] = useState<boolean>(
		db.get("share_api_key") === "true",
	);

	const { updateAuth } = useContext(AuthContext);

	useEffect(() => {
		if (shareAPIKey) return db.set("share_api_key", "true");

		db.set("share_api_key", "false");
	}, [shareAPIKey]);

	return (
		<KeyboardAvoidingView behavior="height" style={styles.loginContainer}>
			<View style={styles.loginBox}>
				{error && <Text style={styles.errorText}>{error}</Text>}

				<TextInput
					title="Hackatime API Key"
					password
					placeholder="aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
					onChange={(event) => {
						setApiKey(event.nativeEvent.text);
					}}
					sideButtonContainerStyle={styles.passwordButton}
				/>

				<Switch
					title="Share API key with the app's server"
					description="If you don't share the API key, you won't be able to receive push notifications or use the goals feature"
					onValueChange={(value) => {
						setShareAPIKey(value);
					}}
					value={shareAPIKey}
				/>

				<Button
					text="Login"
					containerStyle={styles.loginButton}
					onPress={async () => {
						setError(null);

						if (!apiRegex.test(apiKey)) {
							return setError("Invalid API key format");
						}

						const res = await updateAuth(apiKey);

						if (typeof res === "string") {
							return setError(res);
						}
					}}
				/>

				<Text style={styles.noKeyText}>
					Don't have an API key? Get it{" "}
					<Link
						style={styles.linkText}
						href="https://hackatime.hackclub.com/my/settings"
					>
						here
					</Link>
					{"\n"}
					<Link
						style={styles.linkText}
						href="https://hackatime.stefdp.com/privacy"
					>
						Privacy Policy
					</Link>
				</Text>
			</View>
		</KeyboardAvoidingView>
	);
}
