import type { ConfigContext, ExpoConfig } from "expo/config";
// @ts-expect-error "bunx expo install ..." fails without the ".ts"
import { red } from "./constants/hcColors.ts";
import {
	version as appVersion,
	versionCode as appVersionCode,
} from "./package.json";
import { WithAndroidWidgetsParams } from "react-native-android-widget";

const IS_DEV = process.env.APP_VARIANT === "development";
const IS_PRERELEASE = process.env.APP_VARIANT === "prerelease";
// const IS_PRODUCTION = process.env.APP_VARIANT === "production";

let appName = "Hackatime Stats";
let appId = "com.stefdp.hackatime";
let assetsPath = "./assets/images";

if (IS_DEV) {
	appName = "Hackatime Stats (Dev)";
	appId += ".dev";
	assetsPath += "/dev";
}

if (IS_PRERELEASE) {
	appName = "Hackatime Stats (Pre)";
	appId += ".pre";
	assetsPath += "/pre";
}

const widgetConfig: WithAndroidWidgetsParams = {
	// Paths to all custom fonts used in all widgets
	fonts: [
		"./assets/fonts/PhantomSans-Regular.otf",
		"./assets/fonts/PhantomSans-Bold.otf",
		"./assets/fonts/PhantomSans-Italic.otf",
		"./assets/fonts/material-symbols.ttf",
	],
	widgets: [
		{
			name: "Hello", // This name will be the **name** with which we will reference our widget.
			label: "My Hello Widget", // Label shown in the widget picker
			minWidth: "320dp",
			minHeight: "120dp",
			// This means the widget's default size is 5x2 cells, as specified by the targetCellWidth and targetCellHeight attributes.
			// Or 320×120dp, as specified by minWidth and minHeight for devices running Android 11 or lower.
			// If defined, targetCellWidth,targetCellHeight attributes are used instead of minWidth or minHeight.
			targetCellWidth: 5,
			targetCellHeight: 2,
			description: "This is my first widget", // Description shown in the widget picker
			previewImage: "./assets/widget-preview/hello.png", // Path to widget preview image

			// How often, in milliseconds, that this AppWidget wants to be updated.
			// The task handler will be called with widgetAction = 'UPDATE_WIDGET'.
			// Default is 0 (no automatic updates)
			// Minimum is 1800000 (30 minutes == 30 * 60 * 1000).
			updatePeriodMillis: 1800000,
		},
	],
};

export default ({ config }: ConfigContext): ExpoConfig => ({
	...config,
	name: appName,
	slug: "hackatime-stats",
	version: appVersion,
	orientation: "portrait",
	icon: `${assetsPath}/icon.png`,
	scheme: "hackatime-stats",
	newArchEnabled: true,
	userInterfaceStyle: "automatic",
	platforms: ["android"],
	ios: {
		bundleIdentifier: appId,
	},
	android: {
		adaptiveIcon: {
			foregroundImage: `${assetsPath}/adaptive-icon.png`,
			monochromeImage: `${assetsPath}/monochromatic-adaptive-icon.png`,
			backgroundColor: red,
		},
		predictiveBackGestureEnabled: true,
		versionCode: appVersionCode,
		version: appVersion,
		package: appId,
		scheme: appId,
		newArchEnabled: true,
		edgeToEdgeEnabled: true,
	},
	plugins: [
		"expo-router",
		"expo-secure-store",
		"expo-local-authentication",
		"expo-background-task",
		[
			"expo-splash-screen",
			{
				image: `${assetsPath}/splash-icon.png`,
				imageWidth: 300,
				resizeMode: "contain",
				backgroundColor: red,
			},
		],
		// [
		// 	"expo-notifications",
		// 	{
		// 		color: red,
		// 		icon: `${assetsPath}/notification-icon.png`,
		// 		defaultChannel: "motivational-quotes"
		// 	},
		// ],
		[
			"expo-font",
			{
				fonts: [
					"./assets/fonts/PhantomSans-Regular.otf",
					"./assets/fonts/material-symbols.ttf",
				],
				android: {
					fonts: [
						{
							fontFamily: "PhantomSans",
							fontDefinitions: [
								{
									path: "./assets/fonts/PhantomSans-Regular.otf",
									weight: 400,
									style: "normal",
								},
								{
									path: "./assets/fonts/PhantomSans-Italic.otf",
									weight: 400,
									style: "italic",
								},
								{
									path: "./assets/fonts/PhantomSans-Bold.otf",
									weight: 700,
								},
							],
						},
						{
							fontFamily: "MaterialSymbols",
							fontDefinitions: [
								{
									path: "./assets/fonts/material-symbols.ttf",
									weight: 400,
									style: "normal",
								},
							],
						},
					],
				},
			},
		],
		["react-native-android-widget", widgetConfig],
	],
	experiments: {
		typedRoutes: true,
		reactCompiler: true,
	},
	extra: {
		router: {
			origin: false,
		},
		eas: {
			projectId: "cb5c2f76-5092-481c-908d-aa65a1c2f29d",
		},
	},
	runtimeVersion: {
		policy: "appVersion",
	},
});
