import { namedColors } from "@/constants/cssColors";
import type {
	EditorsLast7Days,
	Language,
	LanguageLast7Days,
	OperatingSystemsLast7Days,
	Project,
	ProjectLast7Days,
} from "@/types/hackatime";
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

export function getTop(
	items: (
		| Project
		| ProjectLast7Days
		| Language
		| LanguageLast7Days
		| OperatingSystemsLast7Days
		| EditorsLast7Days
	)[] = [],
): 
| Project
| ProjectLast7Days
| Language
| LanguageLast7Days
| OperatingSystemsLast7Days
| EditorsLast7Days
| null {
	if (items.length === 0) return null;

	return items.reduce((prev, current) =>
		prev.total_seconds > current.total_seconds ? prev : current,
	);
}



export function formatDate(date: Date): `${number}-${number | string}-${number | string}` {
	date.setUTCHours(0, 0, 0, 0);

	const year = date.getFullYear();
	const month = String(date.getMonth() + 1).padStart(2, "0");
	const day = String(date.getDate()).padStart(2, "0");
	
	return `${year}-${month}-${day}`;
}

export function colorHash(str: string) {
	let hash = 0;
	for (let i = 0; i < str.length; i++) {
		hash = str.charCodeAt(i) + ((hash << 5) - hash);
	}

	let color = "#";
	for (let i = 0; i < 3; i++) {
		const value = (hash >> (i * 8)) & 0xff;
		color += `00${value.toString(16)}`.substr(-2);
	}

	return color;
}