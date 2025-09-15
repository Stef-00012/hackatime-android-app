import * as db from "@/functions/database";
import axios from "axios";

interface GetUserStatsParams {
	startDate?: `${number}-${number}-${number}`;
	endDate?: `${number}-${number}-${number}`;
	features?: ("languages" | "projects")[];
}

export async function getUserStats(params?: GetUserStatsParams) {
	const apiKey = db.get("api_key");

	const searchParams = new URLSearchParams();

	if (params?.startDate) searchParams.append("start_date", params.startDate);
	if (params?.endDate) searchParams.append("end_date", params.endDate);
	if (params?.features)
		searchParams.append("features", params.features.join(","));

	try {
		const res = await axios.get(
			"https://hackatime.hackclub.com/api/v1/users/my/stats",
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (_e) {
		console.error(_e);
		return null;
	}
}
