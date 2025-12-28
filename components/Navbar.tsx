import { red, white } from "@/constants/hcColors";
import { navbarOptions } from "@/constants/navbar";
import { styles } from "@/styles/components/navbar";
import { type RelativePathString, usePathname, useRouter } from "expo-router";
import { View } from "react-native";
import Button from "./Button";

export default function Navbar() {
	const pathname = usePathname();
	const router = useRouter();

	return (
		<View style={styles.container}>
			{navbarOptions.map((option) => {
				const isActive = pathname === option.route;

				return (
					<View key={option.route}>
						<Button
							icon={option.icon}
							iconSize={32}
							iconColor={isActive ? red : white}
							type="transparent"
							text={option.name}
							buttonStyle={{
								flexDirection: "column",
							}}
							disabled={isActive}
							containerStyle={{
								borderRadius: 10,
								margin: 5,
							}}
							textStyle={{
								color: isActive ? red : white,
							}}
							onPress={() => {
								router.push(option.route as RelativePathString);
							}}
						/>
					</View>
				);
			})}
		</View>
	);
}
