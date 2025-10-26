import { red, slate } from "@/constants/hcColors";
import {
    type DimensionValue,
    type StyleProp,
    View,
    type ViewStyle,
} from "react-native";

interface Props {
	progress: number;
	height?: DimensionValue;
	width?: DimensionValue;
	progressColor?: string;
	backgroundColor?: string;
	radius?: number;
	style?: StyleProp<ViewStyle>;
}

export default function ProgressBar({
	progress,
	height = 20,
	width,
	progressColor = red,
	backgroundColor = slate,
	radius = 10,
	style,
}: Props) {
	return (
		<View
			style={[
				style,
				{
					height,
					width,
					backgroundColor,
					overflow: "hidden",
					borderRadius: radius,
				},
			]}
		>
			<View
				style={{
					height: "100%",
					width: `${progress}%`,
					borderRadius: radius,
					backgroundColor: progressColor,
				}}
			/>
		</View>
	);
}
