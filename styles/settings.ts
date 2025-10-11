import { elevated, muted, red } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	settingContainer: {
		backgroundColor: elevated,
		borderWidth: 1,
		borderColor: red,
		marginHorizontal: 10,
		borderRadius: 10,
		marginVertical: 5,
		padding: 15,
	},
	settingTitle: {
		fontSize: 30,
		fontWeight: "bold",
	},
	passwordButton: {
		borderRadius: 10,
	},
	button: {
		marginTop: 10,
	},
	containerFooterText: {
		marginTop: 10,
	},
	containerFooterTextTitle: {
		fontWeight: "bold",
	},
	containerFooterTextSubtitle: {
		fontSize: 12,
		color: muted,
	},
});
