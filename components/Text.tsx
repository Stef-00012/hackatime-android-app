import { text } from "@/constants/hcColors";
import { Text as RNText, type TextProps } from "react-native";

type Props = TextProps & {
	style?: TextProps["style"];
};

export default function Text({ style, ...props }: Props) {
	return (
		<RNText
			style={[
				{
					fontFamily: "Hackclub-Regular, MaterialSymbols",
					color: text,
				},
				style,
			]}
			{...props}
		></RNText>
	);
}
