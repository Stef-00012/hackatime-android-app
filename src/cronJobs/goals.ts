import { goalsTitles } from "@/constants/goalsNotifications";
import db, { schema } from "@/db/db";
import { sendPushNotifications } from "@/functions/expo";
import { getCurrentUserTodayData } from "@/functions/hackatime";
import { chunk, formatDate, sleep } from "@/functions/util";
import { and, eq } from "drizzle-orm";
import Expo from "expo-server-sdk";

export async function goalsCronJob() {
	const users = await db.query.users.findMany();

	const expo = new Expo();

	const today = new Date();
	today.setUTCHours(0, 0, 0, 0);

	const userChunks = chunk(users, 200);

	for (const userChunk of userChunks) {
		const isLastChunk = userChunk.length < 200;

		for (const user of userChunk) {
			const apiKey = user.apiKey;

			if (!apiKey) continue;

			const userTodayData = await getCurrentUserTodayData(apiKey);

			if (userTodayData === "Invalid API Key") {
				await db
					.delete(schema.users)
					.where(eq(schema.users.apiKey, user.apiKey));

				continue;
			}

			if (typeof userTodayData === "string") continue;

			const goal = await db.query.goals.findFirst({
				where: eq(schema.goals.apiKey, apiKey),
				orderBy: (goals, { desc }) => [desc(goals.date)],
			});

			if (!goal) continue;

			const notificationsSent = goal.notificationsSent;

			const goalCheckpoints = [25, 50, 75, 100];

			for (const checkpoint of goalCheckpoints) {
				if (
					(userTodayData.grand_total.total_seconds / goal.goal) * 100 >
						checkpoint &&
					!notificationsSent.includes(checkpoint)
				) {
					notificationsSent.push(checkpoint);

					if (!user.expoPushToken || !user.notificationCategories.goals) break;

					const title =
						goalsTitles[Math.floor(Math.random() * goalsTitles.length)];

					sendPushNotifications(expo, [
						{
							to: user.expoPushToken,
							channelId: "goals",
							title: title,
							body: `You just passed ${checkpoint}% of your goal, keep going!`,
							priority: "high",
							sound: "default",
						},
					]);
				}
			}

			if (formatDate(goal.date) !== formatDate(today)) {
				await db
					.insert(schema.goals)
					.values({
						apiKey: apiKey,
						date: today,
						achieved: userTodayData.grand_total.total_seconds,
						goal: goal.goal,
					})
					.onConflictDoUpdate({
						target: [schema.goals.apiKey, schema.goals.date],
						set: {
							achieved: userTodayData.grand_total.total_seconds,
						},
					});

				continue;
			}

			await db
				.update(schema.goals)
				.set({
					achieved: userTodayData.grand_total.total_seconds,
				})
				.where(
					and(eq(schema.goals.apiKey, apiKey), eq(schema.goals.date, today)),
				);
		}

		if (!isLastChunk) await sleep(10 * 1000); // wait 10 seconds between chunks to avoid spamming the API too much
	}
}
