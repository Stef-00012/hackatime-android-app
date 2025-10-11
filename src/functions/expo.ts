import type Expo from "expo-server-sdk";
import type {
    ExpoPushMessage,
    ExpoPushReceipt,
    ExpoPushReceiptId,
    ExpoPushTicket,
} from "expo-server-sdk";

// https://github.com/expo/expo-server-sdk-node#usage

export async function sendPushNotifications(
	expo: Expo,
	notifications: ExpoPushMessage[],
) {
	const notificationChunks = expo.chunkPushNotifications(notifications);

	const tickets: ExpoPushTicket[] = [];

	for (const chunk of notificationChunks) {
		try {
			const chunkTickets = await expo.sendPushNotificationsAsync(chunk);

			tickets.push(...chunkTickets);
		} catch (e) {
			console.error(e);
		}
	}

	const receiptIds: ExpoPushReceiptId[] = [];

	for (const ticket of tickets) {
		if (ticket.status === "ok") receiptIds.push(ticket.id);
	}

	const receiptIdChunks = expo.chunkPushNotificationReceiptIds(receiptIds);

	const receipts: ExpoPushReceipt[] = []

	for (const chunk of receiptIdChunks) {
		try {
            const receipts = await expo.getPushNotificationReceiptsAsync(chunk);

            for (const receiptId in receipts) {
                const { status, details } = receipts[receiptId];

                if (status === "ok") continue;
                else if (status === "error") {
                    console.error("There was an error sending a notification:", details);
                }
            }
        } catch(e) {
            console.error(e);
        }
	}

	return receipts;
}
