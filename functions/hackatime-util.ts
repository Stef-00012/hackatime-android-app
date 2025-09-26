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
		{
			date: Date;
			data: Awaited<ReturnType<typeof getCurrentUserStats>>;
		}
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
