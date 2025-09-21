import { red, white } from "@/constants/hcColors";
import { sidebarOptions } from "@/constants/sidebar";
import { AuthContext } from "@/contexts/AuthProvider";
import { SidebarContext } from "@/contexts/SidebarContext";
import { styles } from "@/styles/components/sidebar";
import type { SidebarOption } from "@/types/sidebar";
import { type RelativePathString, usePathname, useRouter } from "expo-router";
import { useContext, useEffect, useRef, useState } from "react";
import { Animated, Dimensions, View } from "react-native";
import Button from "./Button";

export default function Sidebar() {
	const router = useRouter();
	const pathname = usePathname();

	const { isLoggedIn } = useContext(AuthContext);
	const { open, toggleOpen } = useContext(SidebarContext);

	const [openStates, setOpenStates] = useState<Record<string, boolean>>({});
	const animatedHeights = useRef<Record<string, Animated.Value>>({}).current;

	const screenWidth = Dimensions.get("window").width;
	const translateX = useRef(new Animated.Value(-screenWidth)).current;

	const [displayed, setDisplayed] = useState<boolean>(open);

	useEffect(() => {
		Animated.timing(translateX, {
			toValue: open ? 0 : -screenWidth,
			duration: 300,
			useNativeDriver: true,
		}).start();

		if (open) setDisplayed(true);
		else {
			setTimeout(() => {
				setDisplayed(false);
			}, 300);
		}
	}, [open, screenWidth, translateX]);

	return (
		<Animated.View
			style={[
				styles.sidebar,
				{
					transform: [
						{
							translateX,
						},
					],
					width: screenWidth,
					marginTop: isLoggedIn ? 70 : 0,
					marginBlock: isLoggedIn ? -140 : 0,
				},
			]}
		>
			<View
				style={{
					display: displayed ? undefined : "none",
				}}
			>
				{sidebarOptions.map(renderSidebarOptions)}
			</View>
		</Animated.View>
	);

	function renderSidebarOptions(option: SidebarOption) {
		if (option.type === "button") {
			const isActive = pathname === option.route;

			const route = option.route as RelativePathString;

			return (
				<Button
					key={route}
					type="transparent"
					containerStyle={{
						borderRadius: 7,
						marginHorizontal: 10,
						marginTop: 5,
					}}
					buttonStyle={{
						justifyContent: "flex-start",
					}}
					icon={option.icon}
					onPress={() => {
						toggleOpen(false);

						if (isActive) return;

						router.push(route);
					}}
					textStyle={{
						color: isActive ? red : white,
					}}
					iconColor={isActive ? red : white}
					text={option.name}
					rippleColor={isActive ? undefined : "default"}
				/>
			);
		}

		if (option.type === "select") {
			const selectOpen = openStates[option.name] ?? false;

			if (!animatedHeights[option.name]) {
				animatedHeights[option.name] = new Animated.Value(0);
			}

			const toggleSelect = () => {
				const newOpenState = !selectOpen;

				setOpenStates((prev) => ({
					...prev,
					[option.name]: newOpenState,
				}));

				Animated.timing(animatedHeights[option.name], {
					toValue: newOpenState ? option.subMenus.length * 40 : 0,
					duration: 300,
					useNativeDriver: false,
				}).start();
			};

			return (
				<View key={option.name}>
					<Button
						type="transparent"
						onPress={toggleSelect}
						icon={option.icon}
						text={option.name}
						containerStyle={{
							borderRadius: 7,
							marginHorizontal: 10,
						}}
						textStyle={{
							color: white,
						}}
						buttonStyle={{
							justifyContent: "flex-start",
						}}
						open={selectOpen}
					/>

					<Animated.View
						style={{
							maxHeight: animatedHeights[option.name],
							overflow: "hidden",
						}}
					>
						<View
							style={{
								paddingLeft: 20,
							}}
						>
							{option.subMenus.map(renderSidebarOptions)}
						</View>
					</Animated.View>
				</View>
			);
		}
	}
}
