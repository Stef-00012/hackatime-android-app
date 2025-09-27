import { styles } from "@/styles/components/chartLegend";
import { type ColorValue, View } from "react-native";
import Text from "./Text";

export interface Props {
	data: {
		label: string;
		color: ColorValue;
	}[];
}

export default function ChartLegend({ data }: Props) {
	return (
		<View style={styles.mainLegendContainer}>
			{data.map(({ color, label }) => (
				<View key={label} style={styles.legendContainer}>
					<View
						style={[
							styles.legendColor,
							{
								backgroundColor: color,
							},
						]}
					/>
					<Text>{label}</Text>
				</View>
			))}
		</View>
	);
}
