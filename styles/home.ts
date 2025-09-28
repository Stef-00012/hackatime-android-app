import { darkless, elevated, muted, red, slate } from "@/constants/hcColors";
import { Dimensions, StyleSheet } from "react-native";

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
	chartTitle: {
		fontWeight: "bold",
		fontSize: 24,
		marginBottom: 10,
	},
	mainContent: {
		marginBottom: 70,
	},
	lineChartAxisText: {
		color: muted,
		fontFamily: "PhantomSans",
	},
	lineChartXAxisText: {
		overflow: "visible",
	},
	lineChartYAxisText: {
		textAlign: "right",
	},
	barChartAxisText: {
		color: muted,
		fontFamily: "PhantomSans",
	},
	barChartXAxisText: {
		fontSize: 12,
		transform: [{ rotate: "-120deg" }, { translateX: -5 }, { translateY: -5 }],
	},
	barChartContainer: {
		marginHorizontal: "auto",
		marginBottom: 20,
	},
	barChartPopupTitle: {
		fontWeight: "bold",
		fontSize: 20,
		textAlign: "center",
		marginBottom: 5,
	},
	barChartPopupScrollView: {
		maxHeight: 400,
	},
	barChartPopupProjectContainer: {
		justifyContent: "space-between",
		flexDirection: "row",
		borderWidth: 1,
		borderColor: slate,
		borderRadius: 8,
		padding: 5,
		marginVertical: 5,
	},
	noDataMessage: {
		color: red,
		textAlign: "center",
		fontWeight: "bold",
	},
});

const windowWidth = Dimensions.get("window").width;

export const lineChartWidth =
	windowWidth -
	styles.chartContainer.margin * 2 -
	styles.chartContainer.padding * 2 -
	40;
