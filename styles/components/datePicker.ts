import { darkless, muted, red, white } from "@/constants/hcColors";
import { hexToRgba } from "@/functions/color";
import { StyleSheet } from "react-native";

const redRgba = hexToRgba(red);

const redOverlay = `rgba(${redRgba.r}, ${redRgba.g}, ${redRgba.b}, 0.3)`;

export const styles = StyleSheet.create({
	calendarTextStyle: {
		color: white,
	},
	headerTextStyle: {
		color: white,
	},
	weekDaysTextStyle: {
		color: red,
	},
	yearContainerStyle: {
		backgroundColor: "transparent",
		margin: 5,
		borderWidth: 2,
		borderColor: darkless,
	},
	monthContainerStyle: {
		backgroundColor: "transparent",
		margin: 5,
		borderWidth: 2,
		borderColor: darkless,
	},
	todayTextStyle: {
		fontWeight: "bold",
		color: red,
		fontFamily: "PhantomSans",
	},
	monthSelectorLabel: {
		color: white,
		fontWeight: "bold",
		fontFamily: "PhantomSans",
	},
	yearSelectorLabel: {
		color: white,
		fontWeight: "bold",
		fontFamily: "PhantomSans",
	},
	weekdayLabel: {
		color: muted,
		fontWeight: "bold",
		fontFamily: "PhantomSans",
	},
	dayLabel: {
		color: white,
		fontFamily: "PhantomSans",
	},
	rangeFill: {
		backgroundColor: redOverlay,
	},
	rangeStart: {
		backgroundColor: red,
	},
	rangeEnd: {
		backgroundColor: red,
	},
	selected: {
		backgroundColor: red,
	},
	selectedLabel: {
		color: white,
		fontWeight: "bold",
		fontFamily: "PhantomSans",
	},
	rangeEndLabel: {
		color: white,
		fontWeight: "bold",
		fontFamily: "PhantomSans",
	},
	rangeStartLabel: {
		color: white,
		fontWeight: "bold",
		fontFamily: "PhantomSans",
	},
	rangeMiddleLabel: {
		color: white,
		fontWeight: "bold",
		fontFamily: "PhantomSans",
	},
});
