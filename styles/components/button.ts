import { primary, red, shadowCard, white } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	primaryContainer: {
		overflow: "hidden",
		borderRadius: 99999,
		boxShadow: shadowCard,
		backgroundColor: red,
	},
	primary: {
		alignItems: "center",
		justifyContent: "center",
		paddingHorizontal: 16,
		paddingVertical: 8,
	},
	primaryText: {
		fontWeight: "bold",
		color: white,
	},
	outlineContainer: {
		overflow: "hidden",
		borderRadius: 99999,
		boxShadow: shadowCard,
		backgroundColor: "transparent",
		borderWidth: 2,
		borderStyle: "solid",
		borderColor: primary,
	},
	outline: {
		alignItems: "center",
		justifyContent: "center",
		paddingHorizontal: 16,
		paddingVertical: 8,
	},
	outlineText: {
		fontWeight: "bold",
		color: primary,
	},
});
