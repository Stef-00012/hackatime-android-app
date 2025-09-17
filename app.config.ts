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
		versionCode: appVersionCode,
		version: appVersion,
		package: appId,
		scheme: appId,
		newArchEnabled: true,
		edgeToEdgeEnabled: true,
		permissions: ["REQUEST_INSTALL_PACKAGES"],
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
		// ["expo-image-picker"],
		// ["expo-local-authentication"],
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
			projectId: "ec797fe5-7e0f-4c3a-8aa9-8588d9f44fb1",
		},
	},
	runtimeVersion: {
		policy: "appVersion",
	},
});
