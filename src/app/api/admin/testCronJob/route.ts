import { goalsCronJob } from "@/cronJobs/goals";
import { motivationalNotificationsCronJob } from "@/cronJobs/motivationalNotifications";
import db, { schema } from "@/db/db";
import { eq } from "drizzle-orm";
import { type NextRequest, NextResponse } from "next/server";

export async function POST(req: NextRequest) {
	const apiKey = req.headers.get("Authorization");

	if (!apiKey)
		return NextResponse.json(
			{ error: "Unauthorized", success: false },
			{ status: 401 },
		);

	const admin = await db.query.admins.findFirst({
		where: eq(schema.admins.apiKey, apiKey),
	});

	if (!admin)
		return NextResponse.json(
			{ error: "Unauthorized", success: false },
			{ status: 401 },
		);

	const body = (await req.json()) as {
		cronJob: "goals" | "motivational-notifications";
	};

	const cronJob = body.cronJob;

	if (!["goals", "motivational-notifications"].includes(cronJob))
		return NextResponse.json(
			{ error: "Invalid body", success: false },
			{ status: 400 },
		);

	console.log(`Admin "${admin.username}" started a cronJob: ${cronJob}`);

	switch (cronJob) {
		case "goals":
			await goalsCronJob();
			break;

		case "motivational-notifications":
			await motivationalNotificationsCronJob();
			break;
	}

	return NextResponse.json({ success: true });
}
