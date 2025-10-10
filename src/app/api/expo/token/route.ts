import db, { schema } from "@/db/db";
import { eq } from "drizzle-orm";
import Expo from "expo-server-sdk";
import { type NextRequest, NextResponse } from "next/server";

export async function POST(req: NextRequest) {
	const apiKey = req.headers.get("Authorization");

	if (!apiKey)
		return NextResponse.json(
			{ error: "Unauthorized", success: false },
			{ status: 401 },
		);

	const body = (await req.json()) as {
		expoPushToken: string;
		setAsPrimary?: boolean;
		delete?: boolean;
	};

	if (!body.expoPushToken)
		return NextResponse.json(
			{ error: "Invalid body", success: false },
			{ status: 400 },
		);

	const expoPushToken = body.expoPushToken;
	const setAsPrimary = body.setAsPrimary ?? false;
	const deleteToken = body.delete ?? false;

	if (!Expo.isExpoPushToken(expoPushToken))
		return NextResponse.json(
			{ error: "Invalid Expo Push Token", success: false },
			{ status: 400 },
		);

	const user = await db.query.users.findFirst({
		where: eq(schema.users.apiKey, apiKey),
	});

	if (deleteToken) {
		if (!user) return NextResponse.json({ success: true });
		if (!user.expoPushTokens.includes(expoPushToken))
			return NextResponse.json({ success: true });

		const updatedTokens = user.expoPushTokens.filter(
			(token) => token !== expoPushToken,
		);
		const isPrimary = user.primaryExpoPushToken === expoPushToken;

		await db
			.update(schema.users)
			.set({
				expoPushTokens: updatedTokens,
				primaryExpoPushToken: isPrimary
					? updatedTokens[0] || null
					: user.primaryExpoPushToken,
			})
			.where(eq(schema.users.apiKey, user.apiKey));

		return NextResponse.json({ success: true });
	}

	if (!user) {
		await db.insert(schema.users).values({
			apiKey: apiKey,
			expoPushTokens: [expoPushToken],
			primaryExpoPushToken: expoPushToken,
		});

		return NextResponse.json({ success: true });
	}

	if (user.expoPushTokens.includes(expoPushToken))
		return NextResponse.json({ success: true });

	const updatedTokens = [...user.expoPushTokens, expoPushToken];

	await db
		.update(schema.users)
		.set({
			expoPushTokens: updatedTokens,
			primaryExpoPushToken: setAsPrimary
				? expoPushToken
				: user.primaryExpoPushToken,
		})
		.where(eq(schema.users.apiKey, user.apiKey));

	return NextResponse.json({ success: true });
}
