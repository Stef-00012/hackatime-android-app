import Button from "@/components/Button";
import Slider from "@/components/Slider";
import Switch from "@/components/Switch";
import Text from "@/components/Text";
import { blue, cyan, green } from "@/constants/hcColors";
import * as db from "@/functions/database";
import { getUserGoals } from "@/functions/server";
import { formatDate } from "@/functions/util";
import { styles } from "@/styles/widgetConfiguration/goal";
import type { Goal } from "@/types/server";
import { add } from "date-fns/add";
import * as DocumentPicker from "expo-document-picker";
import { LinearGradient } from "expo-linear-gradient";
import { useEffect, useState } from "react";
import { ImageBackground, PixelRatio, ToastAndroid, View } from "react-native";
import {
	type WidgetConfigurationScreenProps,
	WidgetPreview,
} from "react-native-android-widget";
import { SafeAreaView } from "react-native-safe-area-context";
import { Widget } from "./Widget";

const widgetHeight = PixelRatio.getPixelSizeForLayoutSize(90);
const widgetWidth = PixelRatio.getPixelSizeForLayoutSize(130);

export function ConfigurationScreen({
	widgetInfo,
	setResult,
	renderWidget,
}: WidgetConfigurationScreenProps) {
	const [background, setBackground] = useState<string>(
		db.get("widgetPreviewBackground") || "gradient",
	);

	const [isDarkMode, setIsDarkMode] = useState<boolean>(
		(db.get("Goal_theme") || "dark") === "dark",
	);

	const [transparency, setTransparency] = useState<number>(
		Number.parseInt(db.get("Goal_trasparency") || "60", 10),
	);

	const [goals, setGoals] = useState<Goal[]>([]);

	useEffect(() => {
		(async () => {
			const endDate = new Date();
			const startDate = add(new Date(), {
				weeks: -1,
			});

			const userGoals = await getUserGoals({
				startDate: formatDate(startDate),
				endDate: formatDate(endDate),
			});

			setGoals(userGoals);
		})();
	}, []);

	return (
		<SafeAreaView style={styles.safeAreaViewContainer}>
			<View style={styles.mainContainer}>
				<View>
					<Text style={styles.title}>Widget Configuration</Text>

					<Button
						type="outline"
						text="Change Preview Background"
						onPress={async () => {
							const output = await DocumentPicker.getDocumentAsync({
								type: "image/*",
								multiple: false,
								copyToCacheDirectory: true,
							});

							if (output.canceled || !output.assets.length)
								return ToastAndroid.show(
									"No image selected",
									ToastAndroid.SHORT,
								);

							const file = output.assets[0];

							db.set("widgetPreviewBackground", file.uri);

							setBackground(file.uri);
						}}
					/>

					{background === "gradient" ? (
						<LinearGradient
							colors={[blue, cyan, green]}
							start={{ x: 0, y: 0.5 }}
							end={{ x: 1, y: 0.5 }}
							style={[styles.widgetPreviewContainer]}
						>
							<WidgetPreview
								height={widgetHeight}
								width={widgetWidth}
								renderWidget={(size) => (
									<Widget
										widgetInfo={{
											...widgetInfo,
											height: size.height,
											width: size.width,
										}}
										data={{
											goals,
											theme: isDarkMode ? "dark" : "light",
											transparency,
										}}
									/>
								)}
							/>
						</LinearGradient>
					) : (
						<ImageBackground
							source={{ uri: background }}
							style={[styles.widgetPreviewContainer]}
							borderRadius={16}
						>
							<WidgetPreview
								height={widgetHeight}
								width={widgetWidth}
								renderWidget={(size) => (
									<Widget
										widgetInfo={{
											...widgetInfo,
											height: size.height,
											width: size.width,
										}}
										data={{
											goals,
											theme: isDarkMode ? "dark" : "light",
											transparency,
										}}
									/>
								)}
							/>
						</ImageBackground>
					)}

					<Switch
						value={isDarkMode}
						title="Use Dark Theme"
						onValueChange={() => {
							setIsDarkMode((prev) => !prev);
						}}
					/>

					<Slider
						title="Opacity"
						minimumValue={0}
						value={transparency}
						maximumValue={100}
						onSlidingComplete={(value) => {
							setTransparency(Math.round(value));
						}}
					/>
				</View>

				<View style={styles.buttonsContainer}>
					<Button
						text="Cancel"
						icon="close"
						containerStyle={{ width: "49%" }}
						onPress={() => {
							setResult("cancel");
						}}
					/>

					<Button
						text="Save"
						icon="save"
						containerStyle={{ width: "49%" }}
						onPress={() => {
							db.set("Goal_theme", isDarkMode ? "dark" : "light");
							db.set("Goal_trasparency", String(transparency));

							renderWidget(
								<Widget
									widgetInfo={widgetInfo}
									data={{
										goals,
										theme: isDarkMode ? "dark" : "light",
										transparency,
									}}
								/>,
							);

							setResult("ok");
						}}
					/>
				</View>
			</View>
		</SafeAreaView>
	);
}
