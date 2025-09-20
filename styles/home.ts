import { darkless, elevated, muted } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	scrollView: {
		borderWidth: 2,
		borderColor: darkless,
		marginHorizontal: 10,
		borderRadius: 10,
	},
	statContainer: {
		borderWidth: 2,
		borderColor: darkless,
		backgroundColor: elevated,
		borderRadius: 10,
		padding: 10,
		marginHorizontal: 10,
		marginVertical: 7.5,
	},
	subHeaderText: {
		fontSize: 18,
		color: muted,
		fontWeight: "bold",
	},
	statText: {
		marginTop: 5,
		fontSize: 28,
		fontWeight: "bold",
	},
	rangeButton: {
		marginHorizontal: 10,
	},
	datePickerButton: {
		marginVertical: 10,
	},
	datePickerShowAllTimeButton: {
		marginBottom: 10,
	},
	chartContainer: {
		margin: 10,
		padding: 10,
		justifyContent: "center",
		borderWidth: 2,
		borderColor: darkless,
		backgroundColor: elevated,
		borderRadius: 10,
	},
	pieChartContainer: {
		marginHorizontal: "auto",
	},
});
