import * as db from "@/functions/database";
import { getCurrentUserStats } from "@/functions/hackatime";
import { sendApiKey } from "@/functions/server";
import type { UserStatsResponse } from "@/types/hackatime";
import * as LocalAuthentication from "expo-local-authentication";
import { usePathname, useRouter } from "expo-router";
import {
	createContext,
	type ReactNode,
	useCallback,
	useEffect,
	useMemo,
	useState,
} from "react";
import { ToastAndroid } from "react-native";

interface Props {
	children: ReactNode;
}

interface AuthData {
	user: UserStatsResponse["data"] | string;
	updateAuth: (
		apiKey?: string,
		skipSendApiKey?: boolean,
	) => Promise<UserStatsResponse["data"] | string>;
	isLoggedIn: boolean;
	updateBiometricsSetting: (enabled: boolean) => void;
	requestBiometricsAuthentication: (
		hideBiometricOverlay?: boolean,
		ignoreBiometricAuthState?: boolean,
	) => Promise<boolean>;
	isAuthenticating: boolean;
	unlockWithBiometrics: boolean;
	supportsBiometrics: boolean;
	hasEnrolledBiometrics: boolean;
	supportsAuthenticationTypes: LocalAuthentication.AuthenticationType[];
}

export const AuthContext = createContext<AuthData>({
	user: "Not Logged In",
	updateAuth: async () => "Not Logged In",
	isLoggedIn: false,
	updateBiometricsSetting: () => {},
	requestBiometricsAuthentication: async () => true,
	isAuthenticating: false,
	unlockWithBiometrics: false,
	supportsBiometrics: false,
	hasEnrolledBiometrics: false,
	supportsAuthenticationTypes: [],
});

export default function AuthProvider({ children }: Props) {
	const router = useRouter();
	const pathname = usePathname();

	const [user, setUser] = useState<UserStatsResponse["data"] | string>(
		"Not Logged In",
	);

	const [unlockWithBiometrics, setUnlockWithBiometrics] = useState<boolean>(
		db.get("unlockWithBiometrics") === "true",
	);

	const [supportsBiometrics, setSupportsBiometrics] = useState<boolean>(false);
	const [hasEnrolledBiometrics, setHasEnrolledBiometrics] =
		useState<boolean>(false);
	const [supportsAuthenticationTypes, setSupportsAuthenticationTypes] =
		useState<LocalAuthentication.AuthenticationType[]>([]);

	const [isAuthenticating, setIsAuthenticating] =
		useState<boolean>(unlockWithBiometrics);

	const updateAuth = useCallback(
		async (apiKey?: string, skipSendApiKey?: boolean) => {
			if (typeof apiKey === "string") db.set("api_key", apiKey);

			const user = await getCurrentUserStats({
				features: [],
			});

			if (typeof user === "string" && pathname !== "/login")
				router.replace("/login");
			if (typeof user !== "string" && pathname === "/login")
				router.replace("/");

			setUser(user);

			const shareToServer = db.get("share_api_key") === "true";

			if (shareToServer && !skipSendApiKey) {
				const apiKeySent = await sendApiKey();

				if (!apiKeySent)
					ToastAndroid.show(
						"Failed to send API key to server",
						ToastAndroid.SHORT,
					);
			}

			return user;
		},
		[pathname, router],
	);

	const updateBiometricsSetting = useCallback((enabled: boolean) => {
		setUnlockWithBiometrics(enabled);

		db.set("unlockWithBiometrics", enabled ? "true" : "false");
	}, []);

	const requestBiometricsAuthentication = useCallback(
		async (
			hideBiometricOverlay?: boolean,
			ignoreBiometricAuthState?: boolean,
		) => {
			if (unlockWithBiometrics || ignoreBiometricAuthState) {
				if (!hideBiometricOverlay) setIsAuthenticating(true);

				const output = await LocalAuthentication.authenticateAsync({
					biometricsSecurityLevel: "weak",
					cancelLabel: "Close App",
					promptMessage: "Unlock Hackatime",
					requireConfirmation: true,
				});

				if (output.success) {
					if (!hideBiometricOverlay) setIsAuthenticating(false);

					return true;
				}

				return false;
			}

			return true;
		},
		[unlockWithBiometrics],
	);

	const authData = useMemo<AuthData>(
		() => ({
			user,
			updateAuth,
			isLoggedIn: typeof user !== "string",
			requestBiometricsAuthentication,
			updateBiometricsSetting,
			isAuthenticating,
			unlockWithBiometrics,
			supportsBiometrics,
			hasEnrolledBiometrics,
			supportsAuthenticationTypes,
		}),
		[
			user,
			updateAuth,
			requestBiometricsAuthentication,
			updateBiometricsSetting,
			isAuthenticating,
			unlockWithBiometrics,
			supportsBiometrics,
			hasEnrolledBiometrics,
			supportsAuthenticationTypes,
		],
	);

	useEffect(() => {
		updateAuth(undefined, db.get("share_api_key") !== "true");

		(async () => {
			const hasHardware = await LocalAuthentication.hasHardwareAsync();
			const isEnrolled = await LocalAuthentication.isEnrolledAsync();
			const supportedAuthTypes =
				await LocalAuthentication.supportedAuthenticationTypesAsync();

			setSupportsBiometrics(hasHardware);
			setHasEnrolledBiometrics(isEnrolled);
			setSupportsAuthenticationTypes((prev) => {
				if (JSON.stringify(prev) === JSON.stringify(supportedAuthTypes))
					return prev;

				return supportedAuthTypes;
			});
		})();
	}, [updateAuth]);

	/*
		biome-ignore lint/correctness/useExhaustiveDependencies: adding requestBiometricsAuthentication and unlockWithBiometrics
		causes requestBiometricsAuthentication to run when unlockWithBiometrics changes which i do not want
	*/
	useEffect(() => {
		(async () => {
			if (
				unlockWithBiometrics &&
				supportsBiometrics &&
				hasEnrolledBiometrics &&
				supportsAuthenticationTypes.length > 0
			)
				return await requestBiometricsAuthentication();
		})();
	}, [
		// unlockWithBiometrics,
		hasEnrolledBiometrics,
		supportsAuthenticationTypes,
		supportsBiometrics,
		// requestBiometricsAuthentication,
	]);

	return (
		<AuthContext.Provider value={authData}>{children}</AuthContext.Provider>
	);
}
