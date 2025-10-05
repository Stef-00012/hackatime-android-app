import { StyleSheet } from "react-native";
import { background } from "@/constants/hcColors";

export const styles = StyleSheet.create({
	safeAreaViewContainer: {
		backgroundColor: background,
        flex: 1
	},
    mainContainer: {
        padding: 15,
        flex: 1,
        justifyContent: "space-between",
    },
    title: {
        fontWeight: "bold",
        fontSize: 25,
        marginBottom: 10
    },
    widgetPreviewContainer: {
        padding: 10,
        borderRadius: 16,
        justifyContent: "center",
        alignItems: "center",
        marginTop: 10,
    },
    buttonsContainer: {
        gap: "2%",
        flexDirection: "row"
    }
});
