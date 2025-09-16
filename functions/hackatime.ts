import * as db from "@/functions/database";
import type { CurrentUserRawHeartbeats, UserStatsResponse, UserTodayDataResponse, UserTrustFactor } from "@/types/hackatime";
import axios from "axios";

interface GetUserStatsParams {
	startDate?: `${number}-${number}-${number}`;
	endDate?: `${number}-${number}-${number}`;
	features?: ("languages" | "projects")[];
}

export async function getCurrentUserStats(params?: GetUserStatsParams): Promise<UserStatsResponse | null> {
	const apiKey = db.get("api_key");

	if (!apiKey) return null;

	const searchParams = new URLSearchParams();

	if (params?.startDate) searchParams.append("start_date", params.startDate);
	if (params?.endDate) searchParams.append("end_date", params.endDate);
	if (params?.features)
		searchParams.append("features", params.features.join(","));

	try {
		const res = await axios.get(
			"https://hackatime.hackclub.com/api/v1/users/my/stats",
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (_e) {
		console.error(_e);
		return null;
	}
}

export async function getUserStats(userId: string, params?: GetUserStatsParams): Promise<UserStatsResponse | null> {
	const apiKey = db.get("api_key");

	if (!apiKey) return null;

	const searchParams = new URLSearchParams();

	if (params?.startDate) searchParams.append("start_date", params.startDate);
	if (params?.endDate) searchParams.append("end_date", params.endDate);
	if (params?.features)
		searchParams.append("features", params.features.join(","));

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/users/${userId}/stats`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (_e) {
		console.error(_e);
		return null;
	}
}

export async function getCurrentUserTodayData(): Promise<UserTodayDataResponse | null> {
	const apiKey = db.get("api_key");

	if (!apiKey) return null;

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/hackatime/v1/users/current/statusbar/today`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (_e) {
		console.error(_e);
		return null;
	}
}

export async function getUserTodayData(userId: string): Promise<UserTodayDataResponse | null> {
	const apiKey = db.get("api_key");

	if (!apiKey) return null;

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/hackatime/v1/users/${userId}/statusbar/today`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (_e) {
		console.error(_e);
		return null;
	}
}

export async function getUserTrustFactor(userId: string): Promise<UserTrustFactor | null> {
	const apiKey = db.get("api_key");

	if (!apiKey) return null;

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/users/${userId}/trust_factor`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (_e) {
		console.error(_e);
		return null;
	}
}

interface GetCurrentUserRawHeartbeatsParams {
	startDate?: `${number}-${number}-${number}`;
	endDate?: `${number}-${number}-${number}`;
	limit?: number; // max 100
	mostRecent?: boolean;
}

export async function getCurrentUserRawHeartbeats(params?: GetCurrentUserRawHeartbeatsParams): Promise<CurrentUserRawHeartbeats | null> {
	const apiKey = db.get("api_key");

	if (!apiKey) return null;

	const searchParams = new URLSearchParams();

	if (params?.startDate) searchParams.append("start_date", params.startDate);
	if (params?.endDate) searchParams.append("end_date", params.endDate);
	if (params?.limit)
		searchParams.append("limit", String(params.limit));

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/my/heartbeats${params?.mostRecent ? "/most_recent" : ""}?${searchParams}`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (_e) {
		console.error(_e);
		return null;
	}
}

export async function getUserProjects(userId: string, detailed = false) {
	const apiKey = db.get("api_key");

	if (!apiKey) return null;

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/users/${userId}/projects${detailed ? "/details" : ""}`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (_e) {
		console.error(_e);
		return null;
	}
}

export async function getCurrentUserProjects(detailed = false) {
	const apiKey = db.get("api_key");

	if (!apiKey) return null;

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/users/my/projects${detailed ? "/details" : ""}`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (_e) {
		console.error(_e);
		return null;
	}
}