import db, { schema } from "@/db/db";
import { isHackatimeApiKey } from "@/functions/util";
import { eq } from "drizzle-orm";
import Expo from "expo-server-sdk";
import { type NextRequest, NextResponse } from "next/server";

export async function POST(req: NextRequest) {
	const apiKey = req.headers.get("Authorization");

	if (!apiKey || !isHackatimeApiKey(apiKey, true))
		return NextResponse.json(
			{ error: "Unauthorized", success: false },
			{ status: 401 },
		);

	const body = (await req.json()) as {
		expoPushToken: string;
	};

	if (!body.expoPushToken)
		return NextResponse.json(
			{ error: "Invalid body", success: false },
			{ status: 400 },
		);

	const expoPushToken = body.expoPushToken;

	if (!Expo.isExpoPushToken(expoPushToken))
		return NextResponse.json(
			{ error: "Invalid Expo Push Token", success: false },
			{ status: 400 },
		);

	const user = await db.query.users.findFirst({
		where: eq(schema.users.apiKey, apiKey),
	});

	if (user?.expoPushToken === expoPushToken)
		return NextResponse.json({ success: true });

	await db
		.insert(schema.users)
		.values({
			apiKey: apiKey,
			expoPushToken,
		})
		.onConflictDoUpdate({
			target: schema.users.apiKey,
			set: {
				expoPushToken,
			},
		});

	return NextResponse.json({ success: true });
}
