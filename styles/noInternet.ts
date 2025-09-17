import { background } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	noInternetText: {
		color: "#F6F7F7",
		fontSize: 30,
		fontWeight: "bold",
		margin: "auto",
		justifyContent: "center",
		alignItems: "center",
	},
	noInternetContainer: {
		flex: 1,
		display: "flex",
		backgroundColor: background,
		justifyContent: "center",
		alignItems: "center",
	},
});
