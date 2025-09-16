export interface UserStatsResponse {
	data: {
		username: string;
		user_id: string;
		is_coding_activity_visible: boolean;
		is_other_usage_visible: boolean;
		status: string;
		start: string;
		end: string;
		range: string;
		human_readable_range: string;
		total_seconds: number;
		daily_average: number;
		human_readable_total: string;
		human_readable_daily_average: string;
		languages?: Language[];
		projects?: Project[];
		trust_factor: UserTrustFactor;
	};
}

interface Language {
	name: string;
	total_seconds: number;
	text: string;
	hours: number;
	minutes: number;
	percent: number;
	digital: string;
}

export type Project = Language;

export interface UserTodayDataResponse {
	data: {
		grand_total: {
			text: string;
			total_seconds: number;
		};
	};
}

export interface UserTrustFactor {
	trust_level: "green" | "blue" | "red";
	trust_value: number;
}

export interface CurrentUserRawHeartbeats {
	start_time: string;
	end_time: string;
	total_seconds: number;
	heartbeats: Heartbeat[];
}

export interface Heartbeat {
	id: number;
	user_id: number;
	branch: string;
	category: string;
	dependencies: [];
	editor: string;
	entity: string;
	language: string;
	machine: string;
	operating_system: string;
	project: string;
	type: string;
	user_agent: string;
	line_additions: null;
	line_deletions: null;
	lineno: number;
	lines: number;
	cursorpos: number;
	project_root_count: number;
	time: number;
	is_write: null;
	created_at: string;
	updated_at: string;
	fields_hash: string;
	source_type: string;
	ip_address: string;
	ysws_program: string;
	deleted_at: null;
	raw_data: HeartbeatRawData;
	raw_heartbeat_upload_id: number;
}

export interface HeartbeatRawData {
	time: number;
	type: string;
	lines: number;
	branch: string;
	editor: string;
	entity: string;
	lineno: number;
	machine: string;
	project: string;
	user_id: number;
	category: string;
	is_write: null;
	language: string;
	cursorpos: number;
	user_agent: string;
	dependencies: [];
	line_additions: null;
	line_deletions: null;
	operating_system: string;
	project_root_count: number;
}

export interface GetUserHeartbeatSpansResponse {
	spans: HeartbeatSpan[];
}

export interface HeartbeatSpan {
	start_time: number;
	end_time: number;
	duration: number;
}

export interface GetUserProjectsResponse {
	projects: string[];
}

export interface GetUserProjectDetailsResponse {
	projects: ProjectDetail[];
}

export interface ProjectDetail {
	name: string;
	total_seconds: number;
	languages: string[];
	repo_url: string;
	total_heartbeats: number;
	first_heartbeat: string;
	last_heartbeat: string;
}

export type GetYSWSResponse = string[]