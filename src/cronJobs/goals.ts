import db, { schema } from "@/db/db";
import { getCurrentUserTodayData } from "@/functions/hackatime";
import { chunk, formatDate, sleep } from "@/functions/util";
import { eq } from "drizzle-orm";

export async function goalCronJob() {
	const users = await db.query.users.findMany();

	const userChunks = chunk(users, 200);

	for (const chunk of userChunks) {
		for (const user of chunk) {
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

			if (!goal) {
				await db.insert(schema.goals).values({
					apiKey: apiKey,
					date: new Date(),
					achieved: userTodayData.grand_total.total_seconds,
					goal: 0,
				});

				continue;
			}

			if (formatDate(goal.date) !== formatDate(new Date())) {
				await db
					.insert(schema.goals)
					.values({
						apiKey: apiKey,
						date: new Date(),
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
				.where(eq(schema.goals.apiKey, apiKey));
		}

		await sleep(10 * 1000); // wait 10 seconds between chunks to avoid spamming the API too much
	}
}
