import { primary, red, shadowCard, white } from "@/constants/hcColors";
import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
	primary: {
        borderRadius: 99999,
        alignItems: "center",
        justifyContent: "center",
        boxShadow: shadowCard,
        paddingHorizontal: 16,
        paddingVertical: 8,
		backgroundColor: red,
	},
    primaryText: {
        fontWeight: "bold",
        color: white,
    },
	outline: {
        borderRadius: 99999,
        alignItems: "center",
        justifyContent: "center",
        boxShadow: shadowCard,
        paddingHorizontal: 16,
        paddingVertical: 8,
        backgroundColor: "transparent",
        borderWidth: 2,
        borderStyle: "solid",
        borderColor: primary,
    },
    outlineText: {
        fontWeight: "bold",
        color: primary,
    },
});
