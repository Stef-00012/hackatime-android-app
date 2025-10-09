import { createClient } from "@libsql/client";
import { drizzle } from "drizzle-orm/libsql";
import * as schema from "./schema";

const client = createClient({ url: "file:data/data.db" });

export default drizzle(client, {
	schema,
	logger: process.env.DB_LOGGING === "true",
});

export { schema };
