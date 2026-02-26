import { eq } from "drizzle-orm";

import {
	motivationalMessages,
	motivationalTitles,
} from "@/constants/motivationalNotifications";
import db, { schema } from "@/db/db";
import { getCurrentUserTodayData } from "@/functions/hackatime";
import type { Message } from "firebase-admin/messaging";
import { sendPushNotifications } from "@/functions/firebase";

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

	const notifications: Message[] = [];

	for (const user of userList) {
		const token = user.androidPushToken;

		if (!token) continue;

		const timeZone = user.timeZone;

		if (!isNotificationTime(timeZone)) continue;

		const motivationalMessage =
			motivationalMessages[
				Math.floor(Math.random() * motivationalMessages.length)
			];

		const title =
			motivationalTitles[Math.floor(Math.random() * motivationalTitles.length)];

		const userNotification: Message = {
			token,
			android: {
				priority: "high",
				notification: {
					channelId: "motivational-quotes",
					sound: "default",
					title,
					body: motivationalMessage,
				},
			},
		};

		notifications.push(userNotification);
	}

	await sendPushNotifications(notifications);
}

function isNotificationTime(timezone: string): boolean {
	const formatter = new Intl.DateTimeFormat("en-US", {
		hour: "numeric",
		hour12: false,
		timeZone: timezone,
	});

	const parts = formatter.formatToParts(new Date());
	const hourPart = parts.find((part) => part.type === "hour");

	if (!hourPart) return false;

	const hour = parseInt(hourPart.value, 10);

	return hour === 9 || hour === 21;
}