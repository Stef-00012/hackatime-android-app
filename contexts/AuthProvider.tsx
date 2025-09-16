// import * as LocalAuthentication from "expo-local-authentication";
import { usePathname, useRouter } from "expo-router";
// import { BackHandler } from "react-native";
// import * as db from "@/functions/database";
// import semver from "semver";
import { getCurrentUserStats } from "@/functions/hackatime";
import type { UserStatsResponse } from "@/types/hackatime";
import type React from "react";
import {
	createContext,
	useCallback,
	useEffect,
	useMemo,
	useState,
} from "react";

interface Props {
	children: React.ReactNode;
}

interface AuthData {
	user: UserStatsResponse["data"] | null;
	updateAuth: () => Promise<void>;
	// updateUser: () => Promise<void>;
	// updateBiometricsSetting: (enabled: boolean) => void;
	// requestBiometricsAuthentication: () => Promise<boolean>;
}

export const AuthContext = createContext<AuthData>({
	user: null,
	updateAuth: async () => {},
	// updateUser: async () => {},
	// updateBiometricsSetting: () => {},
	// requestBiometricsAuthentication: async () => true,
});

export default function AuthProvider({ children }: Props) {
	const router = useRouter();
	const pathname = usePathname();

	// const [role, setRole] = useState<APIUser["role"] | false>(false);
	// const [version, setVersion] = useState<string>("0.0.0");

	const [user, setUser] = useState<UserStatsResponse["data"] | null>(null);

	// const [unlockWithBiometrics, setUnlockWithBiometrics] = useState<boolean>(
	// 	db.get("unlockWithBiometrics") === "true",
	// );

	// const [supportsBiometrics, setSupportsBiometrics] = useState<boolean>(false);
	// const [hasEnrolledBiometrics, setHasEnrolledBiometrics] =
	// 	useState<boolean>(false);
	// const [supportsAuthenticationTypes, setSupportsAuthenticationTypes] =
	// 	useState<LocalAuthentication.AuthenticationType[]>([]);

	// const [isAuthenticating, setIsAuthenticating] =
	// 	useState<boolean>(unlockWithBiometrics);

	const updateAuth = useCallback(async () => {
		const user = await getCurrentUserStats();

		if (!user && pathname !== "/login") router.replace("/login");

		setUser(user)
	}, [pathname, router]);

	// const updateUser = useCallback(async () => {
	// 	const currentUser = await getCurrentUser();
	// 	const currentUserAvatar = await getCurrentUserAvatar();

	// 	setUser(typeof currentUser === "string" ? null : currentUser);
	// 	setAvatar(currentUserAvatar);
	// }, []);

	// const updateBiometricsSetting = useCallback((enabled: boolean) => {
	// 	setUnlockWithBiometrics(enabled);
	// 	db.set("unlockWithBiometrics", enabled ? "true" : "false");
	// }, []);

	// const requestBiometricsAuthentication = useCallback(async () => {
	// 	if (unlockWithBiometrics) {
	// 		setIsAuthenticating(true);

	// 		const output = await LocalAuthentication.authenticateAsync({
	// 			biometricsSecurityLevel: "weak",
	// 			cancelLabel: "Close App",
	// 			promptMessage: "Unlock Zipline",
	// 			requireConfirmation: true,
	// 		});

	// 		setIsAuthenticating(false);

	// 		if (output.success) return true;

	// 		return false;
	// 	}

	// 	return true;
	// }, [unlockWithBiometrics]);

	const authData = useMemo<AuthData>(
		() => ({
			user,
			updateAuth,
		}),
		[user, updateAuth],
	);

	useEffect(() => {
		updateAuth();
		// updateUser();

		// (async () => {
		// 	const hasHardware = await LocalAuthentication.hasHardwareAsync();
		// 	const isEnrolled = await LocalAuthentication.isEnrolledAsync();
		// 	const supportedAuthTypes =
		// 		await LocalAuthentication.supportedAuthenticationTypesAsync();

		// 	setSupportsBiometrics(hasHardware);
		// 	setHasEnrolledBiometrics(isEnrolled);
		// 	setSupportsAuthenticationTypes(supportedAuthTypes);
		// })();
	}, [updateAuth]);

	// useEffect(() => {
	// 	(async () => {
	// 		if (unlockWithBiometrics) {
	// 			const authenticated = await requestBiometricsAuthentication();

	// 			if (!authenticated) return BackHandler.exitApp();

	// 			return;
	// 		}
	// 	})();
	// }, [unlockWithBiometrics]);

	return (
		<AuthContext.Provider value={authData}>{children}</AuthContext.Provider>
	);
}
