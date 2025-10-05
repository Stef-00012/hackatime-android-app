import NativeSlider from "@react-native-community/slider";
import { View } from "react-native";
import { red, slate } from "@/constants/hcColors";
import { styles } from "@/styles/components/slider";
import Text from "./Text";

interface Props extends React.ComponentProps<typeof NativeSlider> {
	title?: string;
}

export default function Slider(props: Props) {
	return (
		<View style={styles.sliderContainer}>
			{props.title && <Text style={styles.sliderTitle}>{props.title}</Text>}

			<NativeSlider
				maximumTrackTintColor={red}
				minimumTrackTintColor={red}
				thumbTintColor={slate}
				{...props}
			/>
		</View>
	);
}
