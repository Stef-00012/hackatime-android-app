import Button from "@/components/Button";
import Text from "@/components/Text";
import TextInput from "@/components/TextInput";
import { AuthContext } from "@/contexts/AuthProvider";
import { styles } from "@/styles/login";
import { Link } from "expo-router";
import { useContext, useState } from "react";
import { View } from "react-native";
import { KeyboardAvoidingView } from "react-native-keyboard-controller";

const apiRegex =
	/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

export default function Login() {
	const [error, setError] = useState<string | null>(null);
	const [apiKey, setApiKey] = useState<string>("");

	const { updateAuth } = useContext(AuthContext);

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
				</Text>
			</View>
		</KeyboardAvoidingView>
	);
}
