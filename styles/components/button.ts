import {
	darkless,
	primary,
	red,
	shadowCard,
	white,
} from "@/constants/hcColors";
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
		flexDirection: "row",
		gap: 6,
	},
	primaryText: {
		fontWeight: "bold",
		color: white,
	},
	secondaryContainer: {
		overflow: "hidden",
		borderRadius: 99999,
		boxShadow: shadowCard,
		backgroundColor: darkless,
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
		borderColor: primary,
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
		color: primary,
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
		color: primary,
	},
});
