import { notificationCategories } from "@/constants/notifications";
import * as db from "@/functions/database";
import type { NotificationCategory } from "@/types/notifications";
import type { Goal } from "@/types/server";
import axios from "axios";
import * as Notifications from "expo-notifications";

const serverUrl = __DEV__
	? "http://localhost:3000"
	: "https://hackatime.stefdp.com";

export async function sendApiKey() {
	const apiKey = db.get("api_key");

	try {
		const res = await axios.post(`${serverUrl}/api/user`, null, {
			headers: {
				Authorization: apiKey,
			},
		});

		if (res.data.success) return true;
		return false;
	} catch (_e) {
		return false;
	}
}

export async function sendPushNotificationToken() {
	const apiKey = db.get("api_key");

	const expoPushToken = await Notifications.getExpoPushTokenAsync();

	try {
		const res = await axios.post(
			`${serverUrl}/api/expo/token`,
			{
				expoPushToken: expoPushToken.data,
			},
			{
				headers: {
					Authorization: apiKey,
				},
			},
		);

		if (res.data.success) return true;
		return false;
	} catch (_e) {
		return false;
	}
}

export async function getUserNotificationCategories() {
	const apiKey = db.get("api_key");

	if (!apiKey)
		return notificationCategories.reduce(
			(acc, category) => {
				acc[category] = false;

				return acc;
			},
			{} as Record<NotificationCategory, boolean>,
		);

	try {
		const res = await axios.get(`${serverUrl}/api/notifications`, {
			headers: {
				Authorization: apiKey,
			},
		});

		if (res.data.success)
			return res.data.notificationCategories as Record<
				NotificationCategory,
				boolean
			>;
		return notificationCategories.reduce(
			(acc, category) => {
				acc[category] = false;

				return acc;
			},
			{} as Record<NotificationCategory, boolean>,
		);
	} catch (_e) {
		return notificationCategories.reduce(
			(acc, category) => {
				acc[category] = false;

				return acc;
			},
			{} as Record<NotificationCategory, boolean>,
		);
	}
}

export async function updateUserNotificationCategories(
	categories: Partial<Record<NotificationCategory, boolean>>,
) {
	const apiKey = db.get("api_key");

	if (!apiKey) return false;

	try {
		const res = await axios.patch(
			`${serverUrl}/api/notifications`,
			categories,
			{
				headers: {
					Authorization: apiKey,
				},
			},
		);

		if (res.data.success)
			return res.data.notificationCategories as Record<
				NotificationCategory,
				boolean
			>;
		return false;
	} catch (_e) {
		return false;
	}
}

export async function getUser() {
	const apiKey = db.get("api_key");

	if (!apiKey) return false;

	try {
		const res = await axios.get(`${serverUrl}/api/user`, {
			headers: {
				Authorization: apiKey,
			},
		});

		if (res.data.success) return true;
		return false;
	} catch (_e) {
		return false;
	}
}

export async function deleteUser() {
	const apiKey = db.get("api_key");

	if (!apiKey) return false;

	try {
		const res = await axios.delete(`${serverUrl}/api/user`, {
			headers: {
				Authorization: apiKey,
			},
		});

		if (res.data.success) return true;
		return false;
	} catch (_e) {
		return false;
	}
}

type GoalDate = `${number}-${number | string}-${number | string}`;

interface GetGoalParams {
	date?: GoalDate;
	startDate?: GoalDate;
	endDate?: GoalDate;
	all?: boolean;
}

export async function getUserGoals({
	date,
	endDate,
	startDate,
	all,
}: GetGoalParams): Promise<Goal[]> {
	const apiKey = db.get("api_key");

	if (!apiKey) return [];

	const searchParams = new URLSearchParams();

	if (all) searchParams.append("all", "true");
	if (date) searchParams.append("date", date);
	if (startDate) searchParams.append("start_date", startDate);
	if (endDate) searchParams.append("end_date", endDate);

	try {
		const res = await axios.get(`${serverUrl}/api/goals?${searchParams}`, {
			headers: {
				Authorization: apiKey,
			},
		});

		if (res.data.success) return res.data.goals;
		return [];
	} catch (_e) {
		return [];
	}
}

export async function updateUserGoal(date: GoalDate, goal: number) {
	const apiKey = db.get("api_key");

	if (!apiKey) return false;

	try {
		const res = await axios.patch(
			`${serverUrl}/api/goals`,
			{
				date,
				goal,
			},
			{
				headers: {
					Authorization: apiKey,
				},
			},
		);

		if (res.data.success) return true;
		return false;
	} catch (_e) {
		return false;
	}
}
