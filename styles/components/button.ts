import { shadowCard, white } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	primaryContainer: {
		overflow: "hidden",
		borderRadius: 99999,
		boxShadow: shadowCard,
	},
	primary: {
		alignItems: "center",
		justifyContent: "center",
		paddingHorizontal: 16,
		paddingVertical: 8,
		flexDirection: "row",
		gap: 6,
	},
	primaryText: {
		fontWeight: "bold",
	},
	secondaryContainer: {
		overflow: "hidden",
		borderRadius: 99999,
		boxShadow: shadowCard,
	},
	secondary: {
		alignItems: "center",
		justifyContent: "center",
		paddingHorizontal: 16,
		paddingVertical: 8,
		flexDirection: "row",
		gap: 6,
	},
	secondaryText: {
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
	},
	outline: {
		alignItems: "center",
		justifyContent: "center",
		paddingHorizontal: 16,
		paddingVertical: 8,
		flexDirection: "row",
		gap: 6,
	},
	outlineText: {
		fontWeight: "bold",
	},
	transparentContainer: {
		overflow: "hidden",
		borderRadius: 99999,
		backgroundColor: "transparent",
	},
	transparent: {
		alignItems: "center",
		justifyContent: "center",
		paddingHorizontal: 16,
		paddingVertical: 8,
		flexDirection: "row",
		gap: 6,
	},
	transparentText: {
		fontWeight: "bold",
	},
});
