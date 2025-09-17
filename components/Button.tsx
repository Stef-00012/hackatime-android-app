import { red } from "@/constants/hcColors";
import { getRippleColor } from "@/functions/util";
import { styles } from "@/styles/components/button";
import { Pressable, type StyleProp, Text, type TextStyle, type ViewStyle } from "react-native";

interface Props {
    type?: "primary" | "outline" | "cta";
    onPress?: () => void;
    rippleColor?: string;
    text?: string;
    textStyle?: StyleProp<TextStyle>;
    buttonStyle?: StyleProp<ViewStyle>
}

export default function Button({
    type = "primary",
    onPress,
    rippleColor,
    text,
    textStyle,
    buttonStyle,
}: Props) {
    switch(type) {
        case "outline": {
            return (
                <Pressable android_ripple={{
                    color: rippleColor || getRippleColor(red)
                }} style={[styles.outline, buttonStyle]} onPress={onPress}>
                    {text && <Text style={[styles.outlineText, textStyle]}>{text}</Text>}
                </Pressable>
            );
        }

        case "primary":
        default: {
            return (
                <Pressable android_ripple={{
                    color: rippleColor || getRippleColor(red)
                }} style={[styles.primary, buttonStyle]} onPress={onPress}>
                    {text && <Text style={[styles.primaryText, textStyle]}>{text}</Text>}
                </Pressable>
            );
        }
    }
}