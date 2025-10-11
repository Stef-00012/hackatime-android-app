import next from "next";
import schedule from "node-schedule";
import { createServer } from "node:http";
import { parse } from "node:url";
import { goalsCronJob } from "./cronJobs/goals";
import { motivationalNotificationsCronJob } from "./cronJobs/motivationalNotifications";

const port = Number.parseInt(process.env.PORT || "3000", 10);
const dev = process.env.NODE_ENV !== "production";

const app = next({
	dev,
});

const handle = app.getRequestHandler();

app.prepare().then(async () => {
	const server = createServer((req, res) => {
		if (!req.url) {
			res.statusCode = 400;
			res.end("Bad Request: URL is missing");

			return;
		}

		const parsedUrl = parse(req.url, true);
		handle(req, res, parsedUrl);
	});

	server.listen(port, () => {
		console.info(
			`> \x1b[32mServer listening at \x1b[0;1mhttp://localhost:${port}\n\x1b[0m> \x1b[33mMode: \x1b[0;1m${dev ? "development" : process.env.NODE_ENV}\x1b[0m`,
		);
	});

	if (process.env.NODE_ENV === "production") {
		console.info(
			`\x1b[33m[\x1b[1m${new Date().toISOString()}\x1b[0;33m] \x1b[34mStarted cron jobs\x1b[0m`,
		);

		const motivationalNotifsCronJobInterval = "0 */12 * * *"; // every 12 hours

		schedule.scheduleJob(
			motivationalNotifsCronJobInterval,
			motivationalNotificationsCronJob,
		);

		const goalCronJobInterval = "*/30 * * * *"; // every 30 minutes

		schedule.scheduleJob(goalCronJobInterval, goalsCronJob);
	} else {
		console.info(
			"\x1b[31mCron jobs are not started in development mode\x1b[0m",
		);
	}
});
