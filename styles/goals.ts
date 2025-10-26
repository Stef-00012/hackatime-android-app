import { elevated, red } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	updateGoalButton: {
		marginHorizontal: 10,
		marginTop: 10,
	},
	rangeButton: {
		marginHorizontal: 10,
	},
	datePickerShowAllTimeButton: {
		marginBottom: 10,
	},
	noGoalsContainer: {
		flex: 1,
		justifyContent: "center",
		alignItems: "center",
		marginBottom: 10,
	},
	noGoalsText: {
		fontWeight: "bold",
		fontSize: 18,
	},
	goalsContainer: {
		marginTop: 10,
		flex: 1,
	},
	skeletonGoalContainer: {
		marginHorizontal: 10,
		borderRadius: 10,
		marginVertical: 5,
	},
	goalContainer: {
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
	goalDate: {
		fontWeight: "bold",
		fontSize: 20,
	},
	goalGoal: {
		fontWeight: "bold",
		fontSize: 20,
	},
	headerFooterDivider: {
		height: 30,
	},
	progressHeader: {
		flexDirection: "row",
		alignItems: "center",
		justifyContent: "space-between",
	},
	progressBar: {
		marginTop: 10,
	},
	progressTitle: {
		fontWeight: "bold",
	},
	progressPercentage: {
		fontWeight: "bold",
	},
	updateGoalTitle: {
		fontWeight: "bold",
		fontSize: 22,
		marginBottom: 10,
	},
	updateGoalSaveButton: {
		marginTop: 10,
	},
});
