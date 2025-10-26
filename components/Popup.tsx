import { styles } from "@/styles/components/popup";
import { type ReactNode, useEffect } from "react";
import {
	BackHandler,
	Pressable,
	type StyleProp,
	View,
	type ViewStyle,
} from "react-native";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { Portal } from "react-native-portalize";

interface Props {
	onClose: () => void;
	hidden: boolean;
	children: ReactNode;
	popupStyle?: StyleProp<ViewStyle>;
}

export default function Popup({
	onClose,
	hidden,
	children,
	popupStyle = {},
}: Props) {
	// biome-ignore lint/correctness/useExhaustiveDependencies: Functions should not be parameters of the effect
	useEffect(() => {
		if (!hidden) {
			const { remove } = BackHandler.addEventListener(
				"hardwareBackPress",
				() => {
					onClose();
					remove();

					return true;
				},
			);
		}
	}, [hidden]);

	return (
		<Portal>
			<Pressable
				style={[styles.popupContainerOverlay, hidden && { display: "none" }]}
				onPress={(e) => {
					if (e.target === e.currentTarget) onClose();
				}}
			>
				<View style={[styles.popupContainer, popupStyle]}>
					<KeyboardAwareScrollView showsVerticalScrollIndicator={false}>
						{children}
					</KeyboardAwareScrollView>
				</View>
			</Pressable>
		</Portal>
	);
}
