import type { Props as ChartLegendProps } from "@/components/ChartLegend";
import ms from "enhanced-ms";
import type { Dispatch, SetStateAction } from "react";
import type { lineDataItem, stackDataItem } from "react-native-gifted-charts";
import type {
	getLast7DaysData,
	getProjectsTimelineData,
} from "./hackatime-util";
import { colorHash } from "./util";

export function getLast7DaysChartData(
	last7DaysData: Awaited<ReturnType<typeof getLast7DaysData>>,
): lineDataItem[] {
	return Object.values(last7DaysData)
		.map((dayData) => {
			const totalSeconds =
				typeof dayData.data === "string"
					? 0
					: Array.isArray(dayData.data.languages)
						? dayData.data.languages.reduce(
								(seconds, language) => seconds + language.total_seconds,
								0,
							)
						: 0;

			const value = totalSeconds;

			const label = new Date(dayData.date).toLocaleDateString("en-US", {
				weekday: "short",
			});

			// const dataPointText =
			// 	ms(Number(totalSeconds) * 1000, {
			// 		useAbbreviations: true,
			// 		unitLimit: 1,
			// 	}) || "0s";

			return {
				value,
				label,
				// dataPointText,
			} satisfies lineDataItem;
		})
		.reverse();
}

export function formatLast7DaysChartYAxisLabel(value: string) {
	return (
		ms(Number(value) * 1000, {
			useAbbreviations: true,
			unitLimit: 1,
		}) || "0s"
	);
}

export function getProjectsTimelineChartData(
	timelineData: Awaited<ReturnType<typeof getProjectsTimelineData>>,
	setTimelineDetails: Dispatch<
		SetStateAction<"r1" | "r2" | "r3" | "r4" | "r5" | "r6" | "r7" | null>
	>,
): stackDataItem[] {
	return Object.entries(timelineData)
		.map(([range, rangeData]) => {
			const projects =
				typeof rangeData.data === "string"
					? []
					: Array.isArray(rangeData.data.projects)
						? rangeData.data.projects
						: [];

			// const totalSeconds = projects.reduce((seconds, project) => seconds + project.total_seconds, 0)

			// const value = totalSeconds;

			const label = new Date(rangeData.startDate).toLocaleDateString("en-US", {
				month: "short",
				day: "numeric",
			});

			// const dataPointText =
			// 	ms(Number(totalSeconds) * 1000, {
			// 		useAbbreviations: true,
			// 		unitLimit: 1,
			// 	}) || "0s";

			const stacks: stackDataItem["stacks"] = projects.map((project) => {
				return {
					value: project.total_seconds,
					color: colorHash(project.name),
				};
			});

			return {
				stacks,
				label,
				onPress: () => {
					setTimelineDetails(
						range as "r1" | "r2" | "r3" | "r4" | "r5" | "r6" | "r7",
					);
				},
			} satisfies stackDataItem;
		})
		.reverse();
}

export function getProjectsTimelineChartLegend(
	timelineData: Awaited<ReturnType<typeof getProjectsTimelineData>> | null,
): ChartLegendProps["data"] {
	if (!timelineData) return [];

	const projectsMap = new Map<string, string>();

	for (const rangeData of Object.values(timelineData)) {
		const projects =
			typeof rangeData.data === "string"
				? []
				: Array.isArray(rangeData.data.projects)
					? rangeData.data.projects
					: [];

		for (const project of projects) {
			if (!projectsMap.has(project.name))
				projectsMap.set(project.name, colorHash(project.name));
		}
	}

	return Array.from(projectsMap.entries()).map(([name, color]) => ({
		label: name,
		color,
	}));
}
