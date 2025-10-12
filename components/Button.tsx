import { darkless, muted, red, white } from "@/constants/hcColors";
import { darken } from "@/functions/color";
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
	type?: "primary" | "outline" | "transparent" | "secondary";
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
	open?: boolean;
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
	iconColor,
	iconSize = 20,
	disabled = false,
	open,
}: Props) {
	switch (type) {
		case "outline": {
			const color = disabled ? darken(red, 0.4) : red;

			return (
				<View
					style={[
						styles.outlineContainer,
						{
							borderColor: color,
						},
						containerStyle,
					]}
				>
					<Pressable
						disabled={disabled}
						android_ripple={{
							color:
								!rippleColor || rippleColor === "default"
									? getRippleColor(red)
									: rippleColor,
							foreground: true,
							borderless: true,
						}}
						style={[styles.outline, buttonStyle]}
						onPress={onPress}
					>
						{icon && (
							<MaterialSymbols
								name={icon}
								size={iconSize}
								color={iconColor || color}
							/>
						)}

						{text && (
							<Text style={[styles.outlineText, { color }, textStyle]}>
								{text}
							</Text>
						)}

						{typeof open === "boolean" && (
							<MaterialSymbols
								name={open ? "expand_more" : "expand_less"}
								size={20}
								color="white"
							/>
						)}
					</Pressable>
				</View>
			);
		}

		case "transparent": {
			const color = disabled ? darken(red, 0.4) : red;

			return (
				<View style={[styles.transparentContainer, containerStyle]}>
					<Pressable
						disabled={disabled}
						android_ripple={{
							color:
								!rippleColor || rippleColor === "default"
									? getRippleColor(red)
									: rippleColor,
							foreground: true,
							borderless: true,
						}}
						style={[styles.transparent, buttonStyle]}
						onPress={onPress}
					>
						{icon && (
							<MaterialSymbols
								name={icon}
								size={iconSize}
								color={iconColor || color}
							/>
						)}

						{text && (
							<Text style={[styles.transparentText, { color }, textStyle]}>
								{text}
							</Text>
						)}

						{typeof open === "boolean" && (
							<MaterialSymbols
								name={open ? "expand_more" : "expand_less"}
								size={20}
								color="white"
							/>
						)}
					</Pressable>
				</View>
			);
		}

		case "secondary": {
			const color = disabled ? darken(darkless, 0.4) : darkless;
			const textColor = disabled ? muted : white;

			return (
				<View
					style={[
						styles.secondaryContainer,
						{ backgroundColor: color },
						containerStyle,
					]}
				>
					<Pressable
						disabled={disabled}
						android_ripple={{
							color:
								!rippleColor || rippleColor === "default"
									? getRippleColor(red)
									: rippleColor,
							foreground: true,
							borderless: true,
						}}
						style={[styles.secondary, buttonStyle]}
						onPress={onPress}
					>
						{icon && (
							<MaterialSymbols
								name={icon}
								size={iconSize}
								color={iconColor || textColor}
							/>
						)}

						{text && (
							<Text
								style={[styles.secondaryText, { color: textColor }, textStyle]}
							>
								{text}
							</Text>
						)}

						{typeof open === "boolean" && (
							<MaterialSymbols
								name={open ? "expand_more" : "expand_less"}
								size={20}
								color="white"
							/>
						)}
					</Pressable>
				</View>
			);
		}

		default: {
			const color = disabled ? darken(red, 0.4) : red;
			const textColor = disabled ? muted : white;

			return (
				<View
					style={[
						styles.primaryContainer,
						{ backgroundColor: color },
						containerStyle,
					]}
				>
					<Pressable
						disabled={disabled}
						android_ripple={{
							color:
								!rippleColor || rippleColor === "default"
									? getRippleColor(red)
									: rippleColor,
							foreground: true,
							borderless: true,
						}}
						style={[styles.primary, buttonStyle]}
						onPress={onPress}
					>
						{icon && (
							<MaterialSymbols
								name={icon}
								size={iconSize}
								color={iconColor || textColor}
							/>
						)}

						{text && (
							<Text
								style={[styles.primaryText, { color: textColor }, textStyle]}
							>
								{text}
							</Text>
						)}

						{typeof open === "boolean" && (
							<MaterialSymbols
								name={open ? "expand_more" : "expand_less"}
								size={20}
								color="white"
							/>
						)}
					</Pressable>
				</View>
			);
		}
	}
}
