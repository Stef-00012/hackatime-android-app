import db, { schema } from "@/db/db";
import { isHackatimeApiKey } from "@/functions/util";
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
	})

	if (!user)
		return NextResponse.json(
			{ error: "User not found", success: false },
			{ status: 404 },
		);

	return NextResponse.json({ success: true });
}

export async function POST(req: NextRequest) {
	const apiKey = req.headers.get("Authorization");

	if (!apiKey || !isHackatimeApiKey(apiKey, true))
		return NextResponse.json(
			{ error: "Unauthorized", success: false },
			{ status: 401 },
		);

	await db
		.insert(schema.users)
		.values({
			apiKey: apiKey,
		})
		.onConflictDoNothing();

	return NextResponse.json({ success: true });
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
		timeZone: string;
	};

	const timeZone = body.timeZone;

	if (user.timeZone === timeZone) return NextResponse.json({ success: true });

	if (!Intl.supportedValuesOf("timeZone").includes(timeZone))
		return NextResponse.json(
			{ error: "Invalid time zone", success: false },
			{ status: 400 },
		);

	await db
		.update(schema.users)
		.set({ timeZone })
		.where(eq(schema.users.apiKey, apiKey));

	return NextResponse.json({ success: true });
}

export async function DELETE(req: NextRequest) {
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

	await db.delete(schema.users).where(eq(schema.users.apiKey, apiKey));
	await db.delete(schema.goals).where(eq(schema.goals.apiKey, apiKey));

	return NextResponse.json({ success: true });
}
