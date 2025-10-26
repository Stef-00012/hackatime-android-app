import MaterialSymbols from "@/components/MaterialSymbols";
import Text from "@/components/Text";
import { white } from "@/constants/hcColors";
import { styles } from "@/styles/pages/noInternet";
import { View } from "react-native";

export default function NoInternetPage() {
	return (
		<View style={styles.overlayContainer}>
			<View
				style={{
					display: "flex",
					justifyContent: "center",
					alignItems: "center",
					marginBottom: 20,
				}}
			>
				<MaterialSymbols size={50} name="wifi_off" color={white} />

				<Text style={styles.noInternetText}>No internet connection.</Text>
			</View>
		</View>
	);
}
