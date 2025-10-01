import Button from "@/components/Button";
import MaterialSymbols from "@/components/MaterialSymbols";
import Text from "@/components/Text";
import { red } from "@/constants/hcColors";
import { AuthContext } from "@/contexts/AuthProvider";
import { styles } from "@/styles/pages/biometricAuth";
import { useContext } from "react";
import { View } from "react-native";

export default function BiometricAuthenticationPage() {
	const { isAuthenticating, requestBiometricsAuthentication } =
		useContext(AuthContext);

	if (!isAuthenticating) return null;

	return (
		<View style={styles.overlayContainer}>
			<MaterialSymbols name="lock" color={red} size={50} />

			<Text style={styles.lockedTitle}>Hackatime Locked</Text>

			<Button
				type="transparent"
				text="Unlock"
				containerStyle={styles.unlockButton}
				textStyle={styles.unlockButtonText}
				onPress={requestBiometricsAuthentication}
			/>
		</View>
	);
}
