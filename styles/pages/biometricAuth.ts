import { background } from "@/constants/hcColors";
import { Dimensions, StyleSheet } from "react-native";

const height = Dimensions.get("window").height;

export const styles = StyleSheet.create({
	overlayContainer: {
		...StyleSheet.absoluteFillObject,
		flex: 1,
		display: "flex",
		backgroundColor: background,
		alignItems: "center",
		zIndex: 999998,
		marginBottom: -20,
		paddingTop: 30,
	},
	lockedTitle: {
		color: "#F6F7F7",
		fontSize: 28,
		fontWeight: "bold",
		alignItems: "center",
	},
	unlockButton: {
		position: "absolute",
		bottom: height * (40 / 100),
	},
	unlockButtonText: {
		fontSize: 18,
	},
});
