import db, { schema } from "@/db/db";
import { sendPushNotifications } from "@/functions/expo";
import { eq } from "drizzle-orm";
import { Expo, type ExpoPushMessage } from "expo-server-sdk";
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
		expoPushToken: string;
		notification: ExpoPushMessage;
	};

	const expoPushToken = body.expoPushToken;
	const notificationData = body.notification;

	if (
		!expoPushToken ||
		!Expo.isExpoPushToken(expoPushToken) ||
		!notificationData ||
		!notificationData.title
	)
		return NextResponse.json(
			{ error: "Invalid body", success: false },
			{ status: 400 },
		);

	const expo = new Expo();

	console.info(
		`Admin "${admin.username}" sent a notification to "${expoPushToken}". Notification data:`,
		notificationData,
	);

	const receipts = await sendPushNotifications(expo, [
		{
			...notificationData,
			to: expoPushToken,
		},
	]);

	return NextResponse.json({ success: true, receipt: receipts[0] });
}
