import { muted, slate, white } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	switchContainer: {
		flexDirection: "row",
		marginTop: 10,
		width: "85%",
	},
	switchText: {
		color: white,
		fontSize: 16,
		marginLeft: 5,
		fontWeight: "bold",
		textAlignVertical: "center",
	},
	switchDescription: {
		color: muted,
		marginLeft: 5,
	},
	switchTextDisabled: {
		color: slate,
	},
});
