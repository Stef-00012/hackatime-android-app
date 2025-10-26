import Button from "@/components/Button";
import MaterialSymbols from "@/components/MaterialSymbols";
import Text from "@/components/Text";
import { red } from "@/constants/hcColors";
import { styles } from "@/styles/pages/noApiKeyOnServer";
import { useRouter } from "expo-router";
import { View } from "react-native";

export default function NoApiKeyOnServerPage() {
	const router = useRouter();

	return (
		<View style={styles.overlayContainer}>
			<MaterialSymbols name="key_off" color={red} size={50} />

			<Text style={styles.noApiKeyTitle}>API key not on the server</Text>
			<Text style={styles.noApiKeySubtitle}>
				You must share your API key with the server in order to use the goals
				feature.
			</Text>

			<Button
				type="transparent"
				text="Go to Home"
				containerStyle={styles.goHomeButton}
				textStyle={styles.goHomeButtonText}
				onPress={() => {
					router.replace("/");
				}}
			/>
		</View>
	);
}
