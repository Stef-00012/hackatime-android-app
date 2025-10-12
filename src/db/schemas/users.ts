import type { NotificationCategory } from "@/types/notifications";
import { sqliteTable, text } from "drizzle-orm/sqlite-core";

export const users = sqliteTable("users", {
	apiKey: text("api_key").notNull().primaryKey(),
	expoPushToken: text("expo_push_token").notNull(),
	notificationCategories: text("notification_categories", {
		mode: "json",
	})
		.notNull()
		.default({})
		.$type<Record<NotificationCategory, boolean>>(),
});
