import { messaging } from "@/util/firebase";
import {
	type BatchResponse,
	type Message,
	MessagingClientErrorCode
} from "firebase-admin/messaging";
import { sleep } from "./util";

const invalidCodes: string[] = [
	MessagingClientErrorCode.INVALID_REGISTRATION_TOKEN.code,
	MessagingClientErrorCode.REGISTRATION_TOKEN_NOT_REGISTERED.code,
	MessagingClientErrorCode.INVALID_ARGUMENT.code,
];

export async function validateFCMToken(token: string): Promise<boolean> {
	const message: Message = {
		token,
		notification: {
			title: "Token Validation",
			body: "This is a test notification to validate the FCM token.",
		},
		android: {
			priority: "normal",
		},
	};

	try {
		await messaging.send(message, true);

		return true;
	} catch (e) {
		const err = e as {
			code: string;
			message: string;
		};

		if (invalidCodes.includes(err.code)) {
			return false;
		}

		return false;
	}
}

export async function sendPushNotifications(
	notifications: Message[],
) {
    try {
		if (notifications.length === 1) {
			const messageId = await messaging.send(notifications[0]);

			return messageId;
		}

		const chunks = chunkMessages(notifications)

		const batchResponse: BatchResponse = {
			responses: [],
			successCount: 0,
			failureCount: 0,
		}

		for (const chunk of chunks) {
			const response = await messaging.sendEach(chunk);

			batchResponse.responses.push(...response.responses);
			batchResponse.successCount += response.successCount;
			batchResponse.failureCount += response.failureCount;

			await sleep(10 * 1000)
		}

		return batchResponse;
    } catch(e) {
		console.error(e);
    }
}

export function chunkMessages(messages: Message[]): Message[][] {
	const chunks: Message[][] = [];
	let chunk: Message[] = [];

	let chunkMessagesCount = 0;
	for (const message of messages) {
		chunk.push(message);
		chunkMessagesCount++;

		if (chunkMessagesCount >= 400) {
			chunks.push(chunk);
			chunk = [];
			chunkMessagesCount = 0;
		}
	}

	if (chunkMessagesCount) {
		chunks.push(chunk);
	}

	return chunks;
}
