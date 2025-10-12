import { notificationCategories } from "@/constants/notifications";
import db, { schema } from "@/db/db";
import { isHackatimeApiKey } from "@/functions/util";
import type { NotificationCategory } from "@/types/notifications";
import { eq } from "drizzle-orm";
import { type NextRequest, NextResponse } from "next/server";

export async function GET(req: NextRequest) {
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
		return NextResponse.json({
			notificationCategories: notificationCategories.reduce(
				(acc, category) => {
					acc[category] = false;

					return acc;
				},
				{} as Record<NotificationCategory, boolean>,
			),
			success: true,
		});

	return NextResponse.json({
		notificationCategories: user.notificationCategories,
		success: true,
	});
}

export async function PATCH(req: NextRequest) {
	const apiKey = req.headers.get("Authorization");

	if (!apiKey || !isHackatimeApiKey(apiKey))
		return NextResponse.json(
			{ error: "Unauthorized", success: false },
			{ status: 401 },
		);

	const body = (await req.json()) as Record<NotificationCategory, boolean>;

	const bodyCategories = Object.keys(body) as NotificationCategory[];

	if (
		bodyCategories.length <= 0 ||
		bodyCategories.some(
			(category) => !notificationCategories.includes(category),
		)
	)
		return NextResponse.json(
			{ error: "Invalid body", success: false },
			{ status: 400 },
		);

	const user = await db.query.users.findFirst({
		where: eq(schema.users.apiKey, apiKey),
	});

	if (!user)
		return NextResponse.json(
			{ error: "User not found", success: false },
			{ status: 404 },
		);

	const updatedCategories = {
		...user.notificationCategories,
		...body,
	};

	await db
		.update(schema.users)
		.set({
			notificationCategories: updatedCategories,
		})
		.where(eq(schema.users.apiKey, apiKey));

	return NextResponse.json({
		success: true,
		notificationCategories: updatedCategories,
	});
}
