import { dark, darkless } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	header: {
		flexDirection: "row",
		justifyContent: "space-between",
		alignItems: "center",
		padding: 10,
		backgroundColor: dark,
		borderBottomWidth: 2,
		borderBottomColor: darkless,
		marginBottom: 10,
		zIndex: 10,
	},
	headerLeft: {
		flexDirection: "row",
		gap: 10,
		alignItems: "center",
	},
	username: {
		fontSize: 18,
		fontWeight: "bold",
	},
	button: {
		borderRadius: 10,
	},
});
