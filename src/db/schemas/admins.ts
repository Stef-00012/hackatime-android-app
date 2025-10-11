import { sqliteTable, text } from "drizzle-orm/sqlite-core";

export const admins = sqliteTable("admins", {
    username: text("username").notNull().primaryKey(),
    apiKey: text("api_key").notNull(),
});
