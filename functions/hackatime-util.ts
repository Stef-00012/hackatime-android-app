import { add } from "date-fns/add";
import { getCurrentUserStats } from "./hackatime";
import { formatDate } from "./util";

interface DateData {
	date: Date;
	data: Awaited<ReturnType<typeof getCurrentUserStats>>;
}

export async function getLast7DaysData(): Promise<
	Record<"0d" | "1d" | "2d" | "3d" | "4d" | "5d" | "6d", DateData>
> {
	const last7Days = {
		"0d": new Date(),
		"1d": add(new Date(), { days: -1 }),
		"2d": add(new Date(), { days: -2 }),
		"3d": add(new Date(), { days: -3 }),
		"4d": add(new Date(), { days: -4 }),
		"5d": add(new Date(), { days: -5 }),
		"6d": add(new Date(), { days: -6 }),
	};

	const last7DaysData: Record<
		"0d" | "1d" | "2d" | "3d" | "4d" | "5d" | "6d",
		DateData
	> = {
		"0d": {
			date: last7Days["0d"],
			data: "missing data",
		},
		"1d": {
			date: last7Days["1d"],
			data: "missing data",
		},
		"2d": {
			date: last7Days["2d"],
			data: "missing data",
		},
		"3d": {
			date: last7Days["3d"],
			data: "missing data",
		},
		"4d": {
			date: last7Days["4d"],
			data: "missing data",
		},
		"5d": {
			date: last7Days["5d"],
			data: "missing data",
		},
		"6d": {
			date: last7Days["6d"],
			data: "missing data",
		},
	};

	for (const _day in last7Days) {
		const day = _day as keyof typeof last7Days;

		const startDate = last7Days[day];
		const endDate = add(startDate, { days: 1 });

		const formattedStartDate = formatDate(startDate);
		const formattedEndDate = formatDate(endDate);

		const res = await getCurrentUserStats({
			startDate: formattedStartDate,
			endDate: formattedEndDate,
			features: ["projects", "languages"],
		});

		last7DaysData[day].data = res;
	}

	return last7DaysData;
}

interface TimelineData {
	startDate: Date;
	endDate: Date;
	data: Awaited<ReturnType<typeof getCurrentUserStats>>;
}

export async function getProjectsTimelineData(): Promise<
	Record<"r1" | "r2" | "r3" | "r4" | "r5" | "r6" | "r7", TimelineData>
> {
	const now = new Date();

	const last7Ranges = {
		"r1": {
			startDate: add(now, {
				weeks: -2
			}),
			endDate: now
		},
		"r2": {
			startDate: add(now, {
				weeks: -4
			}),
			endDate: add(now, {
				weeks: -2
			})
		},
		"r3": {
			startDate: add(now, {
				weeks: -6
			}),
			endDate: add(now, {
				weeks: -4
			})
		},
		"r4": {
			startDate: add(now, {
				weeks: -8
			}),
			endDate: add(now, {
				weeks: -6
			})
		},
		"r5": {
			startDate: add(now, {
				weeks: -10
			}),
			endDate: add(now, {
				weeks: -8
			})
		},
		"r6": {
			startDate: add(now, {
				weeks: -12
			}),
			endDate: add(now, {
				weeks: -10
			})
		},
		"r7": {
			startDate: add(now, {
				weeks: -14
			}),
			endDate: add(now, {
				weeks: -12
			})
		},
	};

	const timelineData: Record<
		"r1" | "r2" | "r3" | "r4" | "r5" | "r6" | "r7",
		TimelineData
	> = {
		"r1": {
			startDate: last7Ranges.r1.startDate,
			endDate: last7Ranges.r1.endDate,
			data: "missing data",
		},
		"r2": {
			startDate: last7Ranges.r2.startDate,
			endDate: last7Ranges.r2.endDate,
			data: "missing data",
		},
		"r3": {
			startDate: last7Ranges.r3.startDate,
			endDate: last7Ranges.r3.endDate,
			data: "missing data",
		},
		"r4": {
			startDate: last7Ranges.r4.startDate,
			endDate: last7Ranges.r4.endDate,
			data: "missing data",
		},
		"r5": {
			startDate: last7Ranges.r5.startDate,
			endDate: last7Ranges.r5.endDate,
			data: "missing data",
		},
		"r6": {
			startDate: last7Ranges.r6.startDate,
			endDate: last7Ranges.r6.endDate,
			data: "missing data",
		},
		"r7": {
			startDate: last7Ranges.r7.startDate,
			endDate: last7Ranges.r7.endDate,
			data: "missing data",
		},
	};

	for (const _range in last7Ranges) {
		const rangeNumber = _range as keyof typeof last7Ranges;
		const range = last7Ranges[rangeNumber];

		const startDate = range.startDate;
		const endDate = range.endDate;

		const formattedStartDate = formatDate(startDate);
		const formattedEndDate = formatDate(endDate);

		const res = await getCurrentUserStats({
			startDate: formattedStartDate,
			endDate: formattedEndDate,
			features: ["projects", "languages"],
		});

		timelineData[rangeNumber].data = res;
	}

	return timelineData;
}