import db, { schema } from "@/db/db";
import { isHackatimeApiKey, isValidDate } from "@/functions/util";
import { and, eq, gte, lte } from "drizzle-orm";
import { type NextRequest, NextResponse } from "next/server";

const dateRegex = /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/;

export async function GET(req: NextRequest) {
	const apiKey = req.headers.get("Authorization");

	if (!apiKey || !isHackatimeApiKey(apiKey))
		return NextResponse.json(
			{ error: "Unauthorized", success: false },
			{ status: 401 },
		);

	const url = new URL(req.url);

	const date = url.searchParams.get("date");
	const startDate = url.searchParams.get("start_date");
	const endDate = url.searchParams.get("end_date");
	const all = url.searchParams.get("all") === "true";

	if (all) {
		const goals = await db.query.goals.findMany({
			where: eq(schema.goals.apiKey, apiKey),
			columns: {
				apiKey: false,
			},
			orderBy: (goals, { desc }) => [desc(goals.date)],
		});

		return NextResponse.json({
			success: true,
			goals,
		});
	}

	if (
		startDate &&
		endDate &&
		dateRegex.test(startDate) &&
		dateRegex.test(endDate)
	) {
		const goals = await db.query.goals.findMany({
			where: and(
				eq(schema.goals.apiKey, apiKey),
				gte(schema.goals.date, new Date(startDate)),
				lte(schema.goals.date, new Date(endDate)),
			),
			columns: {
				apiKey: false,
			},
			orderBy: (goals, { desc }) => [desc(goals.date)],
		});

		return NextResponse.json({
			success: true,
			goals,
		});
	}

	if (date && dateRegex.test(date)) {
		const goal = await db.query.goals.findFirst({
			where: and(
				eq(schema.goals.apiKey, apiKey),
				eq(schema.goals.date, new Date(date)),
			),
			columns: {
				apiKey: false,
				notificationsSent: false,
			},
		});

		if (!goal)
			return NextResponse.json({
				success: true,
				goals: [
					{
						date,
						achieved: 0,
						goal: 0,
					},
				],
			});

		return NextResponse.json({
			success: true,
			goals: [goal],
		});
	}

	const todayGoal = await db.query.goals.findFirst({
		where: and(
			eq(schema.goals.apiKey, apiKey),
			eq(schema.goals.date, new Date()),
		),
	});

	if (!todayGoal)
		return NextResponse.json({
			success: true,
			goals: [
				{
					date: new Date(),
					achieved: 0,
					goal: 0,
				},
			],
		});

	return NextResponse.json({
		success: true,
		goals: [todayGoal],
	});
}

export async function PATCH(req: NextRequest) {
	const apiKey = req.headers.get("Authorization");

	if (!apiKey || !isHackatimeApiKey(apiKey))
		return NextResponse.json(
			{ error: "Unauthorized", success: false },
			{ status: 401 },
		);

	const user = await db.query.users.findFirst({
		where: eq(schema.users.apiKey, apiKey),
	});

	if (!user)
		return NextResponse.json(
			{ error: "User not found", success: false },
			{ status: 404 },
		);

	const body = (await req.json()) as {
		date: string;
		goal: number;
	};

	if (
		!body.date ||
		!dateRegex.test(body.date) ||
		!isValidDate(new Date(body.date))
	)
		return NextResponse.json(
			{ error: "Invalid date", success: false },
			{ status: 400 },
		);

	if (
		typeof body.goal !== "number" ||
		body.goal < 60 || // less than 1 minute
		body.goal > 60 * 60 * 23 // more than 23h
	)
		return NextResponse.json(
			{ error: "Invalid goal", success: false },
			{ status: 400 },
		);

	const formattedDate = new Date(new Date(body.date));

	formattedDate.setUTCHours(0, 0, 0, 0);

	const newGoal = await db
		.insert(schema.goals)
		.values({
			apiKey: apiKey,
			date: formattedDate,
			goal: body.goal,
		})
		.onConflictDoUpdate({
			set: {
				goal: body.goal,
			},
			target: [schema.goals.apiKey, schema.goals.date],
		})
		.returning();

	return NextResponse.json(newGoal[0]);
}
