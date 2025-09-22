import { elevated, muted, red } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	noProjectText: {
		fontSize: 32,
		fontWeight: "bold",
		textAlign: "center",
		marginTop: 50,
	},
	projectContainer: {
		backgroundColor: elevated,
		borderWidth: 1,
		borderColor: red,
		marginHorizontal: 10,
		borderRadius: 10,
		marginVertical: 5,
		padding: 15,
	},
	header: {
		flexDirection: "row",
		alignItems: "center",
		justifyContent: "space-between",
	},
	projectName: {
		fontWeight: "bold",
		fontSize: 20,
	},
	buttonContainer: {
		borderRadius: 10,
	},
	button: {
		paddingHorizontal: 10,
		paddingVertical: 6,
	},
	hoursText: {
		color: red,
		fontWeight: "bold",
		fontSize: 22,
		marginVertical: 10,
	},
	languagesText: {
		color: muted,
		flexShrink: 1,
		flexWrap: "wrap",
		alignSelf: "center",
	},
	languagesContainer: {
		flexDirection: "row",
		gap: 5,
	},
	lastHeartbeatText: {
		color: muted,
		flexShrink: 1,
		flexWrap: "wrap",
		alignSelf: "center",
	},
	lastHeartbeatContainer: {
		flexDirection: "row",
		gap: 5,
	},
});
