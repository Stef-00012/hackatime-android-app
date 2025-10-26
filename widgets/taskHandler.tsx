import type { WidgetTaskHandlerProps } from "react-native-android-widget";
import {
	Widget as GoalWidget,
	handleSetup as goalHandleSetup,
	handleUpdate as goalHandleUpdate,
} from "./goal/Widget";
import {
	Widget as TodayTimeWidget,
	handleSetup as todayTimeHandleSetup,
	handleUpdate as todayTimeHandleUpdate,
} from "./todayTime/Widget";
import {
	Widget as TopStatsWidget,
	handleSetup as topStatsHandleSetup,
	handleUpdate as topStatsHandleUpdate,
} from "./topStats/Widget";

export type WidgetNames = "TodayTime" | "TopStats" | "Goal" | "Test";

export async function widgetTaskHandler(props: WidgetTaskHandlerProps) {
	const widgetInfo = props.widgetInfo;
	const widgetName = widgetInfo.widgetName as WidgetNames;

	switch (props.widgetAction) {
		case "WIDGET_ADDED": {
			switch (widgetName) {
				case "TodayTime": {
					const widgetData = await todayTimeHandleSetup();

					props.renderWidget(
						<TodayTimeWidget data={widgetData} widgetInfo={widgetInfo} />,
					);

					break;
				}

				case "TopStats": {
					const widgetData = await topStatsHandleSetup();

					props.renderWidget(
						<TopStatsWidget data={widgetData} widgetInfo={widgetInfo} />,
					);

					break;
				}

				case "Goal": {
					const widgetData = await goalHandleSetup();

					props.renderWidget(
						<GoalWidget data={widgetData} widgetInfo={widgetInfo} />,
					);

					break;
				}

				case "Test": {
					break;
				}
			}

			break;
		}

		case "WIDGET_UPDATE": {
			switch (widgetName) {
				case "TodayTime": {
					const widgetData = await todayTimeHandleUpdate();

					props.renderWidget(
						<TodayTimeWidget data={widgetData} widgetInfo={widgetInfo} />,
					);

					break;
				}

				case "TopStats": {
					const widgetData = await topStatsHandleUpdate();

					props.renderWidget(
						<TopStatsWidget data={widgetData} widgetInfo={widgetInfo} />,
					);

					break;
				}

				case "Goal": {
					const widgetData = await goalHandleUpdate();

					props.renderWidget(
						<GoalWidget data={widgetData} widgetInfo={widgetInfo} />,
					);

					break;
				}

				case "Test": {
					break;
				}
			}

			break;
		}

		case "WIDGET_RESIZED": {
			switch (widgetName) {
				case "TodayTime": {
					const widgetData = await todayTimeHandleUpdate();

					props.renderWidget(
						<TodayTimeWidget data={widgetData} widgetInfo={widgetInfo} />,
					);

					break;
				}

				case "TopStats": {
					const widgetData = await topStatsHandleUpdate();

					props.renderWidget(
						<TopStatsWidget data={widgetData} widgetInfo={widgetInfo} />,
					);

					break;
				}

				case "Goal": {
					const widgetData = await goalHandleUpdate();

					props.renderWidget(
						<GoalWidget data={widgetData} widgetInfo={widgetInfo} />,
					);

					break;
				}

				case "Test": {
					break;
				}
			}

			break;
		}

		case "WIDGET_DELETED": {
			break;
		}

		case "WIDGET_CLICK": {
			break;
		}

		default:
			break;
	}
}
