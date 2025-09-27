import { dark } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	popupContainerOverlay: {
		position: "absolute",
		top: 0,
		left: 0,
		bottom: 0,
		right: 0,
		backgroundColor: "rgba(0, 0, 0, 0.5)",
		zIndex: 3,
		paddingVertical: 40,
		marginVertical: -40,
	},
	popupContainer: {
		maxHeight: 600,
		width: "90%",
		margin: "auto",
		backgroundColor: dark,
		borderRadius: 15,
		padding: 15,
	},
});
