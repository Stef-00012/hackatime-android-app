import { namedColors } from "@/constants/cssColors";
import { isLightColor, rgbaToHex, toRgba } from "./color";

export function getRippleColor(color: string, fraction = 0.4) {
	let hexColor = color;

	if (hexColor in namedColors)
		hexColor = namedColors[hexColor as keyof typeof namedColors];

	if (!hexColor.startsWith("#")) return color;

	const { r, g, b } = toRgba(hexColor);
	const lightColor = isLightColor(hexColor);

	if (lightColor) {
		const newRed = Math.max(0, Math.floor(r * (1 - fraction)));
		const newGreen = Math.max(0, Math.floor(g * (1 - fraction)));
		const newBlue = Math.max(0, Math.floor(b * (1 - fraction)));

		return rgbaToHex(newRed, newGreen, newBlue);
	}

	const newRed = Math.min(255, Math.floor(r + (255 - r) * fraction));
	const newGreen = Math.min(255, Math.floor(g + (255 - g) * fraction));
	const newBlue = Math.min(255, Math.floor(b + (255 - b) * fraction));

	return rgbaToHex(newRed, newGreen, newBlue);
}