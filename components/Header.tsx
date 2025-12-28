import { muted, white } from "@/constants/hcColors";
import { AuthContext } from "@/contexts/AuthProvider";
import { styles } from "@/styles/components/header";
import { usePathname, useRouter } from "expo-router";
import { useContext } from "react";
import { View } from "react-native";
import Button from "./Button";
import Skeleton from "./Skeleton";
import Text from "./Text";

export default function Header() {
	const router = useRouter();
	const pathname = usePathname();

	const { isLoggedIn, user } = useContext(AuthContext);

	return (
		<View style={styles.header}>
			<View style={styles.headerLeft}>
				<Skeleton width={120} height={24} radius="squircle">
					{typeof user !== "string" ? (
						<Text style={styles.username}>{user.username}</Text>
					) : null}
				</Skeleton>
			</View>

			<View>
				<Button
					containerStyle={styles.button}
					type="outline"
					icon="settings"
					iconColor={pathname === "/settings" ? muted : white}
					disabled={
						pathname === "/settings" || !isLoggedIn || typeof user === "string"
					}
					onPress={() => {
						router.push("/settings");
					}}
				/>
			</View>
		</View>
	);
}
