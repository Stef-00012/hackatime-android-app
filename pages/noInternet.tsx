import Text from "@/components/Text";
import { styles } from "@/styles/pages/noInternet";
import { View } from "react-native";

export default function NoInternetPage() {
	return (
		<View style={styles.overlayContainer}>
			<Text style={styles.noInternetText}>No internet connection.</Text>
		</View>
	);
}
