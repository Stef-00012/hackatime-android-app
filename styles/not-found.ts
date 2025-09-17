import { background, white } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	mainContainer: {
		flex: 1,
		display: "flex",
		backgroundColor: background,
		justifyContent: "center",
		alignItems: "center",
	},
	code: {
		color: white,
		fontSize: 90,
		fontWeight: "bold",
		margin: "auto",
	},
	text: {
		color: white,
		fontSize: 30,
		fontWeight: "bold",
		margin: "auto",
		justifyContent: "center",
		alignItems: "center",
	},
	button: {
		marginTop: 10
	}
});
