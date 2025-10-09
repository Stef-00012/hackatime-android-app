import type {
	CurrentUserRawHeartbeats,
	GetUserHeartbeatSpansResponse,
	GetUserProjectDetailsResponse,
	GetUserProjectsResponse,
	GetYSWSResponse,
	ProjectDetail,
	UserStatsLast7DaysResponse,
	UserStatsResponse,
	UserStatsTotalSecondsResponse,
	UserTodayDataResponse,
	UserTrustFactor,
} from "@/types/hackatime";
import axios, { type AxiosError } from "axios";

interface GetUserStatsParams {
	startDate?: `${number}-${number | string}-${number | string}`;
	endDate?: `${number}-${number | string}-${number | string}`;
	features?: ("languages" | "projects")[];
	filterByProject?: string;
	limit?: number;
}

interface GetUserStatsLast7DaysParams {
	features?: (
		| "languages"
		| "projects"
		| "editors"
		| "machines"
		| "operating_systems"
		| "categories"
	)[];
}

export async function getCurrentUserStats(
	apiKey: string,
	params?: GetUserStatsParams,
): Promise<UserStatsResponse["data"] | string> {
	if (!apiKey) return "Missing API Key";

	const searchParams = new URLSearchParams();

	if (params?.startDate) searchParams.append("start_date", params.startDate);
	if (params?.endDate) searchParams.append("end_date", params.endDate);
	if (params?.limit) searchParams.append("limit", String(params.limit));
	if (params?.filterByProject)
		searchParams.append("filter_by_project", params.filterByProject);
	if (params?.features)
		searchParams.append("features", params.features.join(","));

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/users/my/stats?${searchParams}`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data as UserStatsResponse;

		return data.data;
	} catch (e) {
		const error = e as AxiosError;

		if (error.response?.status === 401) return "Invalid API Key";

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}

export async function getCurrentUserStatsLast7Days(
	apiKey: string,
	params: GetUserStatsLast7DaysParams,
): Promise<UserStatsLast7DaysResponse["data"] | string> {
	if (!apiKey) return "Missing API Key";

	const searchParams = new URLSearchParams();

	if (params?.features)
		searchParams.append("features", params.features.join(","));

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/hackatime/v1/users/current/stats/last_7_days?${searchParams}`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data as UserStatsLast7DaysResponse;

		return data.data;
	} catch (e) {
		const error = e as AxiosError;

		if (error.response?.status === 401) return "Invalid API Key";

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}

export async function getCurrentUserStatsTotalSeconds(
	apiKey: string,
): Promise<UserStatsTotalSecondsResponse | string> {
	if (!apiKey) return "Missing API Key";

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/users/my/stats?total_seconds=true`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data as { total_seconds: number };

		return data;
	} catch (e) {
		const error = e as AxiosError;

		if (error.response?.status === 401) return "Invalid API Key";

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}

export async function getUserStats(
	apiKey: string,
	userId: string,
	params?: GetUserStatsParams,
): Promise<UserStatsResponse["data"] | string> {
	if (!apiKey) return "Missing API Key";

	const searchParams = new URLSearchParams();

	if (params?.startDate) searchParams.append("start_date", params.startDate);
	if (params?.endDate) searchParams.append("end_date", params.endDate);
	if (params?.features)
		searchParams.append("features", params.features.join(","));

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/users/${userId}/stats`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data as UserStatsResponse;

		return data.data;
	} catch (e) {
		const error = e as AxiosError;

		if (error.response?.status === 401) return "Invalid API Key";

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}

export async function getCurrentUserTodayData(
	apiKey: string,
): Promise<UserTodayDataResponse["data"] | string> {
	if (!apiKey) return "Missing API Key";

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/hackatime/v1/users/current/statusbar/today`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data as UserTodayDataResponse;

		return data.data;
	} catch (e) {
		const error = e as AxiosError;

		if (error.response?.status === 401) return "Invalid API Key";

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}

export async function getUserTodayData(
	apiKey: string,
	userId: string,
): Promise<UserTodayDataResponse["data"] | string> {
	if (!apiKey) return "Missing API Key";

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/hackatime/v1/users/${userId}/statusbar/today`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data as UserTodayDataResponse;

		return data.data;
	} catch (e) {
		const error = e as AxiosError;

		if (error.response?.status === 401) return "Invalid API Key";

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}

export async function getUserTrustFactor(
	apiKey: string,
	userId: string,
): Promise<UserTrustFactor | string> {
	if (!apiKey) return "Missing API Key";

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/users/${userId}/trust_factor`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (e) {
		const error = e as AxiosError;

		if (error.response?.status === 401) return "Invalid API Key";

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}

interface GetCurrentUserRawHeartbeatsParams {
	startDate?: `${number}-${number | string}-${number | string}`;
	endDate?: `${number}-${number | string}-${number | string}`;
	limit?: number; // max 100
	mostRecent?: boolean;
}

export async function getCurrentUserRawHeartbeats(
	apiKey: string,
	params?: GetCurrentUserRawHeartbeatsParams,
): Promise<CurrentUserRawHeartbeats | string> {
	if (!apiKey) return "Missing API Key";

	const searchParams = new URLSearchParams();

	if (params?.startDate) searchParams.append("start_date", params.startDate);
	if (params?.endDate) searchParams.append("end_date", params.endDate);
	if (params?.limit) searchParams.append("limit", String(params.limit));

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/my/heartbeats${params?.mostRecent ? "/most_recent" : ""}?${searchParams}`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (e) {
		const error = e as AxiosError;

		if (error.response?.status === 401) return "Invalid API Key";

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}

interface GetUserHeartbeatSpansParams {
	startDate?: `${number}-${number | string}-${number | string}`;
	endDate?: `${number}-${number | string}-${number | string}`;
	project?: string;
	filterByProject?: string[];
}

export async function getUserHeartbeatSpans(
	apiKey: string,
	userId: string,
	params?: GetUserHeartbeatSpansParams,
): Promise<GetUserHeartbeatSpansResponse | string> {
	if (!apiKey) return "Missing API Key";

	const searchParams = new URLSearchParams();

	if (params?.startDate) searchParams.append("start_date", params.startDate);
	if (params?.endDate) searchParams.append("end_date", params.endDate);
	if (params?.project) searchParams.append("project", params.project);
	if (params?.filterByProject)
		searchParams.append("filter_by_project", params.filterByProject.join(","));

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/users/${userId}/heartbeats/spans?${searchParams}`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (e) {
		const error = e as AxiosError;

		if (error.response?.status === 401) return "Invalid API Key";

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}

export async function getUserProjects(
	apiKey: string,
	userId: string,
	detailed?: false,
): Promise<GetUserProjectsResponse | string>;
export async function getUserProjects(
	apiKey: string,
	userId: string,
	detailed?: true,
): Promise<GetUserProjectDetailsResponse | string>;
export async function getUserProjects(
	apiKey: string,
	userId: string,
	detailed = false,
): Promise<GetUserProjectsResponse | GetUserProjectDetailsResponse | string> {
	if (!apiKey) return "Missing API Key";

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/users/${userId}/projects${detailed ? "/details" : ""}`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (e) {
		const error = e as AxiosError;

		if (error.response?.status === 401) return "Invalid API Key";

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}

export async function getCurrentUserProjects(
	apiKey: string,
	detailed?: false,
): Promise<GetUserProjectsResponse | string>;
export async function getCurrentUserProjects(
	apiKey: string,
	detailed?: true,
): Promise<GetUserProjectDetailsResponse | string>;
export async function getCurrentUserProjects(
	apiKey: string,
	detailed = false,
): Promise<GetUserProjectsResponse | GetUserProjectDetailsResponse | string> {
	if (!apiKey) return "Missing API Key";

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/users/my/projects${detailed ? "/details" : ""}`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (e) {
		const error = e as AxiosError;

		if (error.response?.status === 401) return "Invalid API Key";

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}

export async function getCurrentUserProject(
	apiKey: string,
	projectName: string,
): Promise<ProjectDetail | string> {
	if (!apiKey) return "Missing API Key";

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/users/my/project/${projectName}`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (e) {
		const error = e as AxiosError;

		if (error.response?.status === 401) return "Invalid API Key";

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}

export async function getUserProject(
	apiKey: string,
	userId: string,
	projectName: string,
): Promise<ProjectDetail | string> {
	if (!apiKey) return "Missing API Key";

	try {
		const res = await axios.get(
			`https://hackatime.hackclub.com/api/v1/users/${userId}/project/${projectName}`,
			{
				headers: {
					Authorization: `Bearer ${apiKey}`,
				},
			},
		);

		const data = res.data;

		return data;
	} catch (e) {
		const error = e as AxiosError;

		if (error.response?.status === 401) return "Invalid API Key";

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}

export async function getYSWSPrograms(): Promise<GetYSWSResponse | string> {
	try {
		const res = await axios.get(
			"https://hackatime.hackclub.com/api/v1/ysws_programs",
		);

		const data = res.data;

		return data;
	} catch (e) {
		const error = e as AxiosError;

		const response = error.response?.data as { error?: string } | undefined;

		if (response?.error) {
			return response.error;
		}

		return "Something went wrong...";
	}
}
