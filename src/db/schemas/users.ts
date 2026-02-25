import type { NotificationCategory } from "@/types/notifications";
import { sqliteTable, text } from "drizzle-orm/sqlite-core";

export const users = sqliteTable("users", {
	apiKey: text("api_key").notNull().primaryKey(),
	androidPushToken: text("android_push_token"),
	notificationCategories: text("notification_categories", {
		mode: "json",
	})
		.notNull()
		.default({})
		.$type<Record<NotificationCategory, boolean>>(),
});
