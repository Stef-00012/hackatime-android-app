import { styles } from "@/styles/components/header";
import { usePathname, useRouter } from "expo-router";
import { View } from "react-native";
import Button from "./Button";
import Text from "./Text";
import Skeleton from "./skeleton/Skeleton";

interface Props {
	username: string | null;
}

export default function Header({ username }: Props) {
	const router = useRouter();
	const pathname = usePathname();

	return (
		<View style={styles.header}>
			<View>
				<Skeleton width={120} height={24} radius="squircle">
					{username ? <Text style={styles.username}>{username}</Text> : null}
				</Skeleton>
			</View>

			<View>
				<Button
					containerStyle={styles.button}
					type="outline"
					icon="settings"
					disabled={pathname === "/settings"}
					onPress={() => {
						router.push("/settings");
					}}
				/>
			</View>
		</View>
	);
}
