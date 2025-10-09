import db, { schema } from "@/db/db";
import { getCurrentUserTodayData } from "@/functions/hackatime";
import { eq } from "drizzle-orm";

const minimumCodeTime = 60 * 60; // 1h

export async function getMotivationalNotificationsUsersList() {
	const users = await db.query.users.findMany();

	const notifUsers: (typeof schema.users.$inferSelect)[] = [];

	for (const user of users) {
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
