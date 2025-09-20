import { colors } from "@/constants/skeleton";
import { Skeleton as NativeSkeleton } from "moti/skeleton";
import type { ReactElement } from "react";
import { type DimensionValue, View } from "react-native";

interface Props {
	children?: ReactElement | null;
	disableAnimation?: boolean;
	width?: DimensionValue;
	height?: DimensionValue;
	radius?: "round" | "square" | "squircle" | number;
}

function Skeleton({
	children,
	disableAnimation,
	width,
	height,
	radius = "square",
}: Props) {
	if (disableAnimation) {
		if (radius === "square") radius = 0;
		if (radius === "squircle") radius = 10;
		if (radius === "round") radius = 9999;

		return (
			<View
				style={{
					width,
					height,
					borderRadius: radius || 10,
					backgroundColor: colors[1],
				}}
			>
				{children}
			</View>
		);
	}

	return (
		<NativeSkeleton
			colors={colors}
			width={width}
			height={height}
			radius={radius === "squircle" ? 10 : radius}
		>
			{children}
		</NativeSkeleton>
	);
}

Skeleton.Group = NativeSkeleton.Group;

export default Skeleton;
