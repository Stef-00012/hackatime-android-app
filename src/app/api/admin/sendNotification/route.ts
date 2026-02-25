import db, { schema } from "@/db/db";
import { eq } from "drizzle-orm";
import { type NextRequest, NextResponse } from "next/server";
import { messaging } from "@/util/firebase";
import type { Message } from "firebase-admin/messaging";
import { validateFCMToken } from "@/functions/firebase";

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
		androidPushToken: string;
		notification: Message;
	};

	const androidPushToken = body.androidPushToken;
	const notificationData = body.notification;

	if (
		!androidPushToken ||
		!(await validateFCMToken(androidPushToken)) ||
		!notificationData ||
		!notificationData.android?.notification?.title
	)
		return NextResponse.json(
			{ error: "Invalid body", success: false },
			{ status: 400 },
		);

	console.info(
		`Admin "${admin.username}" sent a notification to "${androidPushToken}". Notification data:`,
		notificationData,
	);

	try {
		const messageId = await messaging.send({
			token: androidPushToken,
			...notificationData,
		});

		return NextResponse.json({ success: true, messageId });
	} catch (_e) {
		return NextResponse.json(
			{ error: "Failed to send notification", success: false },
			{ status: 500 },
		);
	}
}
