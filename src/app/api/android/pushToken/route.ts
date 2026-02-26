import db, { schema } from "@/db/db";
import { validateFCMToken } from "@/functions/firebase";
import { isHackatimeApiKey } from "@/functions/util";
import { eq } from "drizzle-orm";
import { type NextRequest, NextResponse } from "next/server";

export async function POST(req: NextRequest) {
	const apiKey = req.headers.get("Authorization");

	if (!apiKey || !isHackatimeApiKey(apiKey, true))
		return NextResponse.json(
			{ error: "Unauthorized", success: false },
			{ status: 401 },
		);

	const body = (await req.json()) as {
		androidPushToken: string;
		timeZone: string;
	};

	if (!body.androidPushToken || !body.timeZone)
		return NextResponse.json(
			{ error: "Invalid body", success: false },
			{ status: 400 },
		);

	const androidPushToken = body.androidPushToken;

	const isTokenValid = await validateFCMToken(androidPushToken);

	if (!isTokenValid)
		return NextResponse.json(
			{ error: "Invalid FCM Token", success: false },
			{ status: 400 },
		);

	const timeZone = body.timeZone;

	if (!Intl.supportedValuesOf("timeZone").includes(timeZone)) 
		return NextResponse.json(
			{ error: "Invalid time zone", success: false },
			{ status: 400 },
		);

	const user = await db.query.users.findFirst({
		where: eq(schema.users.apiKey, apiKey),
	});

	if (user?.androidPushToken === androidPushToken && user.timeZone === timeZone) {
		return NextResponse.json({
			success: true,
		});
	}

	await db
		.insert(schema.users)
		.values({
			apiKey,
			androidPushToken,
			timeZone,
		})
		.onConflictDoUpdate({
			target: schema.users.apiKey,
			set: {
				androidPushToken,
				timeZone,
			},
		});

	return NextResponse.json({
		success: true,
	});
}
