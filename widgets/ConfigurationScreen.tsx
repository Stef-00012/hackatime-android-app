import type { WidgetConfigurationScreenProps } from "react-native-android-widget";
import type { WidgetNames } from "./taskHandler";
import { ConfigurationScreen as TodayTimeConfigurationScreen } from "./todayTime/Widget";
import { ConfigurationScreen as TopStatsConfigurationScreen } from "./topStats/Widget";

export function ConfigurationScreen({
	widgetInfo,
	setResult,
	renderWidget,
}: WidgetConfigurationScreenProps) {
	const widgetName = widgetInfo.widgetName as WidgetNames;

	switch (widgetName) {
		case "TodayTime": {
			return (
				<TodayTimeConfigurationScreen
					renderWidget={renderWidget}
					setResult={setResult}
					widgetInfo={widgetInfo}
				/>
			);
		}

		case "TopStats": {
			return (
				<TopStatsConfigurationScreen
					renderWidget={renderWidget}
					setResult={setResult}
					widgetInfo={widgetInfo}
				/>
			);
		}

		case "Test": {
			break;
		}
	}

	/*
        biome-ignore lint/complexity/noUselessFragments: Fragment is required as
        registerWidgetConfigurationScreen in index.ts expects a React component
    */
	return <></>;
}
