import type { ConfigContext, ExpoConfig } from "expo/config";
import {
	version as appVersion,
	versionCode as appVersionCode,
} from "./package.json";

const IS_DEV = process.env.APP_VARIANT === "development";
const IS_PRERELEASE = process.env.APP_VARIANT === "prerelease";
// const IS_PRODUCTION = process.env.APP_VARIANT === "production";

let appName = "Hackatime";
let appId = "com.stefdp.hackatime";
let assetsPath = "./assets/images";

if (IS_DEV) {
	appName = "Hackatime (Dev)";
	appId += ".dev";
	assetsPath += "/dev";
}

if (IS_PRERELEASE) {
	appName = "Hackatime (Pre)";
	appId += ".pre";
	assetsPath += "/pre";
}

export default ({ config }: ConfigContext): ExpoConfig => ({
	...config,
	name: appName,
	slug: "hackatime",
	version: appVersion,
	orientation: "portrait",
	icon: `${assetsPath}/icon.png`,
	scheme: "hackatime",
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
		// predictiveBackGestureEnabled: true,
		versionCode: appVersionCode,
		version: appVersion,
		package: appId,
		scheme: appId,
		newArchEnabled: true,
		edgeToEdgeEnabled: true,
	},
	plugins: [
		"expo-router",
		"expo-font",
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
			projectId: "0818e8e4-d637-43be-a6d8-041bac2bf273",
		},
	},
	runtimeVersion: {
		policy: "appVersion",
	},
});
