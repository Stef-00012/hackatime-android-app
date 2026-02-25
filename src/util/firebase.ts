import admin from "firebase-admin";
import fs from "node:fs";
import path from "node:path";

const serviceAccountPath = path.join(process.cwd(), "firebase-adminsdk.json");

if (fs.existsSync(serviceAccountPath)) {
	const serviceAccount = JSON.parse(
		fs.readFileSync(serviceAccountPath, "utf8"),
	);

	if (!admin.apps.length) {
		admin.initializeApp({
			credential: admin.credential.cert(serviceAccount),
		});
	}
} else {
	console.error(
		"Firebase service account file not found at:",
		serviceAccountPath,
	);
}

export const messaging = admin.messaging();
