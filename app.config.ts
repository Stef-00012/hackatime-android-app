import type { ConfigContext, ExpoConfig } from "expo/config";
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
			backgroundColor: "#ec3750",
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
						}
					],
				},
			},
		],
		[
			"expo-splash-screen",
			{
				image: `${assetsPath}/splash-icon.png`,
				imageWidth: 300,
				resizeMode: "contain",
				backgroundColor: "#ec3750",
			},
		],
		["expo-secure-store"],
		["expo-local-authentication"],
		// ["expo-image-picker"],
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
