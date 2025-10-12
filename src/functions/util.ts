import { promisify } from "node:util";
import { getCurrentUserTodayData } from "./hackatime";

export const sleep = promisify(setTimeout);

export function chunk<T>(
	items: T[],
	chunkSize: number,
): T[][] {
	const chunks = [];

	for (let i = 0; i < items.length; i += chunkSize) {
		chunks.push(items.slice(i, i + chunkSize));
	}

	return chunks;
}

export function formatDate(date: Date): string {
	const year = date.getFullYear();
	const month = String(date.getMonth() + 1).padStart(2, '0');
	const day = String(date.getDate()).padStart(2, '0');

	const formatted = `${year}-${month}-${day}`;
	
	return formatted;
}

// today or future date
export function isValidDate(date: Date): boolean {
	const today = new Date();
	today.setUTCHours(0, 0, 0, 0);

	date.setUTCHours(0, 0, 0, 0);

	return date.getTime() >= today.getTime();
}

export async function isHackatimeApiKey(apiKey: string, checkApi = false) {
	if (!/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(apiKey)) return false;

	if (!checkApi) return true;

	const userData = await getCurrentUserTodayData(apiKey);

	if (userData === "Invalid API Key") return false;

	return true;
}