import { background, darkless, muted, red } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	loginContainer: {
		display: "flex",
		flex: 1,
		backgroundColor: background,
		justifyContent: "center",
		alignContent: "center",
	},
	loginBox: {
		width: "80%",
		borderWidth: 2,
		borderColor: darkless,
		borderStyle: "solid",
		padding: 10,
		borderRadius: 10,
		margin: "auto",
	},
	errorText: {
		color: red,
		fontWeight: "bold",
		textAlign: "center",
	},
	loginButton: {
		marginTop: 10,
		marginBottom: 5,
	},
	passwordButton: {
		borderRadius: 10,
	},
	noKeyText: {
		color: muted,
	},
	linkText: {
		color: red,
	},
});
