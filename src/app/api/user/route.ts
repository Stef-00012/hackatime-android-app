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
