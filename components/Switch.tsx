import { darkless, muted, red, slate } from "@/constants/hcColors";
import { styles } from "@/styles/components/switch";
import { Switch as NativeSwitch } from "@react-native-material/core";
import { View } from "react-native";
import Text from "./Text";
import { darken } from "@/functions/color";

interface Props {
    value: boolean;
    title?: string;
    description?: string;
    onValueChange: (value: boolean, id?: Props["id"]) => void | Promise<void>;
    disabled?: boolean;
    id?: string;
}

export default function Switch({
	value,
	title,
	description,
	onValueChange,
	disabled = false,
	id,
}: Props) {
    return (
        <View style={styles.switchContainer}>
            <NativeSwitch
                disabled={disabled}
                value={value}
                onValueChange={(value) => onValueChange(value, id)}
                thumbColor={value
                    ? disabled ? darken(red, 0.4) : red
                    : disabled ? darken(darkless, 0.4) : darkless
                }
                trackColor={{
                    true: disabled ? darken(muted, 0.6) : muted,
                    false: disabled ? darken(slate, 0.4) : slate,
                }}
            />

            {title && (
                <View style={{ flexDirection: "column" }}>
                    <Text
                        style={[
                            styles.switchText,
                            ...(disabled ? [styles.switchTextDisabled] : []),
                        ]}
                    >
                        {title}
                    </Text>

                    {description && (
                        <Text style={styles.switchDescription}>{description}</Text>
                    )}
                </View>
            )}
        </View>
    )
}