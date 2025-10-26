import type { ConfigContext, ExpoConfig } from "expo/config";
import type { WithAndroidWidgetsParams } from "react-native-android-widget";
// @ts-expect-error "bunx expo install ..." fails without the ".ts"
import { red } from "./constants/hcColors.ts";
import {
	version as appVersion,
	versionCode as appVersionCode,
} from "./package.json";

const IS_DEV = process.env.APP_VARIANT === "development";
const IS_PRERELEASE = process.env.APP_VARIANT === "prerelease";
// const IS_PRODUCTION = process.env.APP_VARIANT === "production";

let appName = "Hackatime Stats";
let appId = "com.stefdp.hackatime";
let assetsPath = "./assets/images";

const widgetConfig: WithAndroidWidgetsParams = {
	fonts: [
		"./assets/fonts/PhantomSans-Regular.otf",
		"./assets/fonts/PhantomSans-Bold.otf",
		"./assets/fonts/PhantomSans-Italic.otf",
		"./assets/fonts/material-symbols.ttf",
	],
	widgets: [
		/*
			https://developer.android.com/develop/ui/views/appwidgets/layouts#estimate-minimum-dimensions

			size: n x m    (n & m = number of cells)

			"minWidth": (73 * cells - 16)dp
			"minHeight": (66 * cells - 15)dp

			"maxResizeWidth": (73 * (cells + 1) - 16)dp
			"maxResizeHeight": (66 * (cells) + 11)dp
		*/

		// 2x1
		{
			name: "TodayTime",
			label: "Today's Coding Hours",
			minWidth: "130dp", // 2 cells
			minHeight: "51dp", // 1 cells
			targetCellWidth: 2,
			targetCellHeight: 1,
			description:
				"Displays the hours you coded today. Updates every 30 minutes",
			previewImage: "./assets/widget-preview/todayTime.png",
			updatePeriodMillis: 30 * 60 * 1000,
			widgetFeatures: "reconfigurable|configuration_optional",
		},

		// 2x1 to 5x2
		{
			name: "TopStats",
			label: "Last 7 Days Top Stats",
			minWidth: "130dp", // 2 cells
			minHeight: "51dp", // 1 cell
			targetCellWidth: 5,
			targetCellHeight: 2,
			maxResizeWidth: "422dp", // 5 cells
			maxResizeHeight: "143dp", // 2 cells
			resizeMode: "horizontal|vertical",
			description:
				"Display your top language, project, editor, machine and OS of the last 7 days. Updates every 30 minutes",
			previewImage: "./assets/widget-preview/topStats.png",
			updatePeriodMillis: 30 * 60 * 1000,
			widgetFeatures: "reconfigurable|configuration_optional",
		},

		// 2x1 to 5x2
		{
			name: "Goal",
			label: "Hour Goal",
			minWidth: "130dp", // 2 cells
			minHeight: "51dp", // 1 cell
			targetCellWidth: 5,
			targetCellHeight: 2,
			maxResizeWidth: "422dp", // 5 cells
			maxResizeHeight: "143dp", // 2 cells
			resizeMode: "horizontal|vertical",
			description:
				"Display a goal of hours chosen by you. Updates every 30 minutes",
			previewImage: "./assets/widget-preview/goal.png",
			updatePeriodMillis: 30 * 60 * 1000,
			widgetFeatures: "reconfigurable|configuration_optional",
		},
	],
};

if (IS_PRERELEASE) {
	appName = "Hackatime Stats (Pre)";
	appId += ".pre";
	assetsPath += "/pre";
}

if (IS_DEV) {
	appName = "Hackatime Stats (Dev)";
	appId += ".dev";
	assetsPath += "/dev";

	// any size
	widgetConfig.widgets.push({
		name: "Test",
		label: "Test Widget",
		minWidth: "0dp",
		minHeight: "0dp",
		targetCellWidth: 1,
		targetCellHeight: 1,
		resizeMode: "horizontal|vertical",
		description: "This is a development test widget",
		previewImage: "./assets/widget-preview/testWidget.png",
		// updatePeriodMillis: 30 * 60 * 1000,
		updatePeriodMillis: 0, // no periodical update
		widgetFeatures: "reconfigurable|configuration_optional",
	});
}

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
	description: "An android app made to view your Hackatime stats & projects.",
	primaryColor: red,
	githubUrl: "https://github.com/Stef-00012/hackatime-android-app",
	owner: "stef_dp",
	ios: {
		bundleIdentifier: appId,
	},
	android: {
		adaptiveIcon: {
			foregroundImage: `${assetsPath}/adaptive-icon.png`,
			monochromeImage: `${assetsPath}/monochromatic-adaptive-icon.png`,
			backgroundColor: red,
		},
		googleServicesFile: IS_DEV ? "../google-services.json" : "./google-services.json",
		playStoreUrl: "https://play.google.com/store/apps/details?id=com.stefdp.hackatime",
		backgroundColor: red,
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
		"expo-document-picker",
		// "./plugins/wallpaperReader/withWallpaperReader.js",
		["react-native-android-widget", widgetConfig],
		[
			"expo-dev-client",
			{
				launchMode: "most-recent",
				addGeneratedScheme: false,
			},
		],
		[
			"expo-splash-screen",
			{
				image: `${assetsPath}/splash-icon.png`,
				imageWidth: 300,
				resizeMode: "contain",
				backgroundColor: red,
			},
		],
		[
			"expo-notifications",
			{
				color: red,
				icon: `${assetsPath}/notification-icon.png`,
				defaultChannel: "default"
			},
		],
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
