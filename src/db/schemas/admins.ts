import { sqliteTable, text } from "drizzle-orm/sqlite-core";

export const admins = sqliteTable("admins", {
    username: text("username").notNull().primaryKey(),
    passwordHash: text("password_hash").notNull(),
});
