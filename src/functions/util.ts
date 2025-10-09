import { promisify } from "node:util";

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
	today.setHours(0, 0, 0, 0);

	date.setHours(0, 0, 0, 0);

	return date.getTime() >= today.getTime();
}