CREATE TABLE `admins` (
	`username` text PRIMARY KEY NOT NULL,
	`api_key` text NOT NULL
);
--> statement-breakpoint
CREATE TABLE `goals` (
	`api_key` text NOT NULL,
	`date` integer NOT NULL,
	`goal` integer DEFAULT 0 NOT NULL,
	`achieved` integer DEFAULT 0 NOT NULL,
	`notifications_sent` text DEFAULT '[]' NOT NULL,
	PRIMARY KEY(`api_key`, `date`)
);
--> statement-breakpoint
CREATE TABLE `users` (
	`api_key` text PRIMARY KEY NOT NULL,
	`expo_push_token` text,
	`notification_categories` text DEFAULT '{}' NOT NULL
);
