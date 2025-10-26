import { integer, primaryKey, sqliteTable, text } from "drizzle-orm/sqlite-core";

export const goals = sqliteTable("goals", {
    apiKey: text("api_key").notNull(),
    date: integer("date", {
        mode: "timestamp_ms"
    }).notNull(),
    goal: integer("goal").notNull().default(0),
    achieved: integer("achieved").notNull().default(0),
    notificationsSent: text("notifications_sent", {
        mode: "json"
    })
        .notNull()
        .default([])
        .$type<number[]>()
}, (table) => [
    primaryKey({
        columns: [
            table.apiKey,
            table.date
        ]
    })
]);
