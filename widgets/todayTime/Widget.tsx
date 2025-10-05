// import React from 'react';

import {
	FlexWidget,
	TextWidget,
	type WidgetInfo,
} from "react-native-android-widget";
import { background, red, text, white } from "@/constants/hcColors";
import * as db from "@/functions/database";
import { getCurrentUserTodayData } from "@/functions/hackatime";

export { ConfigurationScreen } from "./ConfigurationScreen";

interface WidgetData {
	transparency: number;
	theme: "light" | "dark";
	hours: string;
}

export interface Props {
	data: WidgetData;
	widgetInfo?: WidgetInfo;
}

export function Widget({ data }: Props) {
	"use no memo";

	const transparency = (Math.round((data.transparency / 100) * 255)).toString(16).padStart(2, '0');

	const backgroundColor = data.theme === "dark" ? background : white;
	const textColor = data.theme === "dark" ? text : background;

	return (
		<FlexWidget
			style={{
				height: "match_parent",
				width: "match_parent",
				justifyContent: "center",
				alignItems: "center",
				backgroundColor: `${backgroundColor}${transparency}`,
				borderRadius: 16,
			}}
		>
			<TextWidget
				text="Today's Hours"
				style={{
					fontSize: 20,
					fontFamily: "PhantomSans-Regular",
					color: red,
					marginBottom: 4,
					fontWeight: "bold",
				}}
			/>
			<TextWidget
				text={data?.hours || "0s"}
				style={{
					fontSize: 32,
					fontFamily: "PhantomSans-Regular",
					color: textColor,
				}}
			/>
		</FlexWidget>
	);
}

export async function handleUpdate(): Promise<WidgetData> {
	const todayHours = await getCurrentUserTodayData();

	const transparency = Number.parseInt(db.get("TodayTime_trasparency") || "60", 10);
	const theme = (db.get("TodayTime_theme") as WidgetData["theme"]) || "dark";

	return typeof todayHours === "string"
		? {
				hours: "0s",
				transparency,
				theme,
			}
		: {
				hours: todayHours.grand_total.text,
				transparency,
				theme,
		};
}

export const handleSetup = handleUpdate;