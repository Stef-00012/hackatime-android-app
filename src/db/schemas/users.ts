import { sqliteTable, text } from "drizzle-orm/sqlite-core";

export const users = sqliteTable("users", {
	apiKey: text("api_key")
		.notNull()
		.primaryKey(),
	expoPushTokens: text("expo_push_tokens", {
		mode: "json"
	})
		.default([])
		.notNull()
		.$type<string[]>(),
	primaryExpoPushToken: text("primary_expo_push_token"),
});
