import { red, white } from "@/constants/hcColors";
import { getRippleColor } from "@/functions/util";
import { styles } from "@/styles/components/button";
import {
	type ColorValue,
	Pressable,
	type StyleProp,
	type TextStyle,
	View,
	type ViewStyle,
} from "react-native";
import MaterialSymbols from "./MaterialSymbols";
import Text from "./Text";

interface Props {
	type?: "primary" | "outline";
	onPress?: () => void;
	rippleColor?: string;
	text?: string;
	textStyle?: StyleProp<TextStyle>;
	buttonStyle?: StyleProp<ViewStyle>;
	containerStyle?: StyleProp<ViewStyle>;
	icon?: keyof typeof MaterialSymbols.glyphMap;
	iconColor?: ColorValue;
	iconSize?: number;
	disabled?: boolean;
}

export default function Button({
	type = "primary",
	onPress,
	rippleColor,
	text,
	textStyle,
	buttonStyle,
	containerStyle,
	icon,
	iconColor = white,
	iconSize = 20,
	disabled = false,
}: Props) {
	switch (type) {
		case "outline": {
			return (
				<View style={[styles.outlineContainer, containerStyle]}>
					<Pressable
						disabled={disabled}
						android_ripple={{
							color: rippleColor || getRippleColor(red),
							foreground: true,
							borderless: true,
						}}
						style={[styles.outline, buttonStyle]}
						onPress={onPress}
					>
						{icon && (
							<MaterialSymbols name={icon} size={iconSize} color={iconColor} />
						)}

						{text && (
							<Text style={[styles.outlineText, textStyle]}>{text}</Text>
						)}
					</Pressable>
				</View>
			);
		}

		// case "primary":
		default: {
			return (
				<View style={[styles.primaryContainer, containerStyle]}>
					<Pressable
						disabled={disabled}
						android_ripple={{
							color: rippleColor || getRippleColor(red),
							foreground: true,
							borderless: true,
						}}
						style={[styles.primary, buttonStyle]}
						onPress={onPress}
					>
						{icon && (
							<MaterialSymbols name={icon} size={iconSize} color={iconColor} />
						)}

						{text && (
							<Text style={[styles.primaryText, textStyle]}>{text}</Text>
						)}
					</Pressable>
				</View>
			);
		}
	}
}
