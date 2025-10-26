import { background } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	overlayContainer: {
		...StyleSheet.absoluteFillObject,
		flex: 1,
		top: 50,
		display: "flex",
		backgroundColor: background,
		alignItems: "center",
		zIndex: 3,
		// marginBottom: -40,
		paddingTop: 30,
	},
	noApiKeyTitle: {
		fontSize: 28,
		fontWeight: "bold",
		alignItems: "center",
	},
	noApiKeySubtitle: {
		fontSize: 18,
		textAlign: "center",
		marginHorizontal: 20,
	},
	goHomeButton: {
		position: "absolute",
		bottom: "50%",
	},
	goHomeButtonText: {
		fontSize: 18,
	},
});
