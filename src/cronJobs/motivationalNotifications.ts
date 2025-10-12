import { eq } from "drizzle-orm";
import Expo, { type ExpoPushMessage } from "expo-server-sdk";

import {
	motivationalMessages,
	motivationalTitles,
} from "@/constants/motivationalNotifications";
import db, { schema } from "@/db/db";
import { sendPushNotifications } from "@/functions/expo";
import { getCurrentUserTodayData } from "@/functions/hackatime";

const minimumCodeTime = 60 * 60; // 1h

async function getMotivationalNotificationsUsersList() {
	const users = await db.query.users.findMany();

	const notifUsers: (typeof schema.users.$inferSelect)[] = [];

	for (const user of users) {
		if (!user.notificationCategories["motivational-quotes"]) continue;

		const todayData = await getCurrentUserTodayData(user.apiKey);

		if (todayData === "Invalid API Key") {
			await db.delete(schema.users).where(eq(schema.users.apiKey, user.apiKey));

			continue;
		}

		if (typeof todayData === "string") continue;

		const userTodaySeconds = todayData.grand_total.total_seconds;

		if (userTodaySeconds < minimumCodeTime) {
			notifUsers.push(user);
		}
	}

	return notifUsers;
}

export async function motivationalNotificationsCronJob() {
	const userList = await getMotivationalNotificationsUsersList();

	const notifications: ExpoPushMessage[] = [];

	for (const user of userList) {
		const token = user.expoPushToken;

		if (!token) continue;

		const motivationalMessage =
			motivationalMessages[
				Math.floor(Math.random() * motivationalMessages.length)
			];

		const title =
			motivationalTitles[
				Math.floor(Math.random() * motivationalTitles.length)
			];

		const userNotification: ExpoPushMessage = {
			to: token,
			channelId: "motivational-quotes",
			title: title,
			body: motivationalMessage,
			priority: "high",
			sound: "default",
		}

		notifications.push(userNotification);
	}

	const expo = new Expo();

	await sendPushNotifications(expo, notifications);
}
