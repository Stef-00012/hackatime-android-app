import { styles } from "@/styles/components/datePicker";
import type { ReactNode } from "react";
import DateTimePicker, { useDefaultStyles } from "react-native-ui-datepicker";
import Button from "./Button";
import Popup from "./Popup";

type DateTimePickerProps = Parameters<typeof DateTimePicker>[0];

type Props = DateTimePickerProps & {
	open: boolean;
	onClose: () => void | Promise<void>;
	children?: ReactNode;
};

export default function DatePicker(props: Props) {
	const { open, onClose, children, ...datePickerProps } = props;

	const defaultStyles = useDefaultStyles();

	return (
		<Popup hidden={!open} onClose={onClose}>
			<DateTimePicker
				{...datePickerProps}
				locale="en"
				showOutsideDays
				firstDayOfWeek={1}
				styles={{
					...defaultStyles,
					day_label: styles.dayLabel,
					today_label: styles.todayTextStyle,
					weekday_label: styles.weekdayLabel,
					selected_label: styles.selectedLabel,
					range_end_label: styles.rangeEndLabel,
					range_start_label: styles.rangeStartLabel,
					range_middle_label: styles.rangeMiddleLabel,
					year_selector_label: styles.yearSelectorLabel,
					month_selector_label: styles.monthSelectorLabel,

					month: styles.monthContainerStyle,
					year: styles.yearContainerStyle,

					range_end: styles.rangeEnd,
					range_fill: styles.rangeFill,
					range_start: styles.rangeStart,

					selected: styles.selected,
				}}
			/>

			{children}

			<Button text="Close" onPress={onClose} type="primary" />
		</Popup>
	);
}
