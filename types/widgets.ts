import type React from "react";
import type { WidgetConfigurationScreenProps } from "react-native-android-widget";
import type { Props as TodayTimeProps } from "@/widgets/todayTime/Widget";

export interface Widgets {
	TodayTime: {
		widget: (props: TodayTimeProps) => React.JSX.Element;
		updateHandler?: () => Promise<TodayTimeProps["data"]> | TodayTimeProps["data"];
		setupHandler?: () => Promise<TodayTimeProps["data"]> | TodayTimeProps["data"];
		configurationScreen?: (props: WidgetConfigurationScreenProps) => React.JSX.Element;
	};
	TopStats: {
		widget: () => React.JSX.Element;
		updateHandler?: () => Promise<unknown> | unknown;
		setupHandler?: () => Promise<unknown> | unknown;
		configurationScreen?: (props: WidgetConfigurationScreenProps) => React.JSX.Element;
	};
	Test: {
		widget: () => React.JSX.Element;
		updateHandler?: () => Promise<unknown> | unknown;
		setupHandler?: () => Promise<unknown> | unknown;
		configurationScreen?: (props: WidgetConfigurationScreenProps) => React.JSX.Element;
	};
}
