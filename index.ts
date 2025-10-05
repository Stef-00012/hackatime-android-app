import "expo-router/entry";
import {
	registerWidgetConfigurationScreen,
	registerWidgetTaskHandler,
} from "react-native-android-widget";
import { ConfigurationScreen } from "./widgets/ConfigurationScreen";
import { widgetTaskHandler } from "./widgets/taskHandler";

registerWidgetTaskHandler(widgetTaskHandler);
registerWidgetConfigurationScreen(ConfigurationScreen);
