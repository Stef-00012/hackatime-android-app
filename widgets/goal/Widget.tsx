import {
	background,
	elevated,
	muted,
	red,
	slate,
	smoke,
	text,
	white,
} from "@/constants/hcColors";
import * as db from "@/functions/database";
import { getUserGoals } from "@/functions/server";
import { formatDate, getDatesBetween } from "@/functions/util";
import type { Goal } from "@/types/server";
import { add } from "date-fns/add";
import ms from "enhanced-ms";
import {
	type ColorProp,
	FlexWidget,
	SvgWidget,
	TextWidget,
	type WidgetInfo,
} from "react-native-android-widget";

export { ConfigurationScreen } from "./ConfigurationScreen";

interface WidgetData {
	transparency: number;
	theme: "light" | "dark";
	goals: Goal[];
}

export interface Props {
	data: WidgetData;
	widgetInfo?: WidgetInfo;
}

const materialIconsTimerSVG = `
<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#e3e3e3">
	<path d="M360-840v-80h240v80H360Zm80 440h80v-240h-80v240Zm40 320q-74 0-139.5-28.5T226-186q-49-49-77.5-114.5T120-440q0-74 28.5-139.5T226-694q49-49 114.5-77.5T480-800q62 0 119 20t107 58l56-56 56 56-56 56q38 50 58 107t20 119q0 74-28.5 139.5T734-186q-49 49-114.5 77.5T480-80Zm0-80q116 0 198-82t82-198q0-116-82-198t-198-82q-116 0-198 82t-82 198q0 116 82 198t198 82Zm0-280Z"/>
</svg>
`;

export function Widget({ data, widgetInfo }: Props) {
	"use no memo";

	const today = new Date();
	today.setUTCHours(0, 0, 0, 0);

	const todayGoal =
		data.goals.find((goal) => goal.date === today.toISOString()) ||
		data.goals[data.goals.length - 1];

	const mainTransparencyNumber = Math.round((data.transparency / 100) * 255);
	const mainTransparency = mainTransparencyNumber.toString(16).padStart(2, "0");

	const boxTransparencyNumber = Math.min(mainTransparencyNumber + 80, 255);
	const boxTransparency = boxTransparencyNumber.toString(16).padStart(2, "0");

	const backgroundColor = data.theme === "dark" ? background : white;
	const boxBackground = data.theme === "dark" ? elevated : smoke;
	const progressBarBackground = data.theme === "dark" ? muted : slate;
	const progressBarDisabledBackground = data.theme === "dark" ? slate : muted;

	const textColor = data.theme === "dark" ? text : background;
	const secondaryTextColor = data.theme === "dark" ? muted : slate;

	if (!todayGoal || data.goals.length === 0) {
		return (
			<FlexWidget
				style={{
					height: "match_parent",
					width: "match_parent",
					flexDirection: "column",
					justifyContent: "center",
					backgroundColor: `${backgroundColor}${mainTransparency}`,
					borderRadius: 16,
				}}
			>
				<TextWidget
					text="No goal data"
					style={{
						textAlign: "center",
						width: "match_parent",
						height: "match_parent",
						fontSize: 32,
						fontWeight: "bold",
						color: textColor,
					}}
				/>
			</FlexWidget>
		);
	}

	const height = widgetInfo?.height || 115;
	const width = widgetInfo?.width || 170;

	const cellsWidth = Math.round(width / 91);
	const cellsHeight = Math.round(height / 126);

	let padding = 8;

	if (cellsWidth > 3 && cellsHeight === 1) {
		return (
			<FlexWidget
				style={{
					height: "match_parent",
					width: "match_parent",
					flexDirection: "row",
					justifyContent: "space-between",
					backgroundColor: `${backgroundColor}${mainTransparency}`,
					borderRadius: 16,
					padding,
				}}
			>
				<FlexWidget
					style={{
						flexDirection: "row",
						alignItems: "center",
						height: "match_parent",
					}}
				>
					<FlexWidget
						style={{
							borderRadius: 9999,
							padding,
							margin: padding,
							height: "match_parent",
							width: height - padding * 4,
							backgroundColor: `${boxBackground}${boxTransparency}`,
						}}
					>
						<SvgWidget
							svg={materialIconsTimerSVG}
							style={{
								width: "match_parent",
								height: "match_parent",
							}}
						/>
					</FlexWidget>

					<TextWidget
						text={
							ms(todayGoal.achieved * 1000, {
								unitLimit: 2,
								useAbbreviations: true,
							}) || `${todayGoal.achieved}s`
						}
						style={{
							color: textColor,
							fontSize: 32,
							fontWeight: "bold",
						}}
					/>
				</FlexWidget>

				<FlexWidget
					style={{
						alignItems: "center",
						flexDirection: "column",
						height: "match_parent",
						justifyContent: "center",
					}}
				>
					<TextWidget
						text={`Target: ${
							ms(todayGoal.goal * 1000, {
								unitLimit: 2,
								useAbbreviations: true,
							}) || `${todayGoal.goal}s`
						}`}
						style={{
							color: secondaryTextColor,
							fontSize: 15,
							textAlign: "right",
							marginBottom: 5,
						}}
					/>

					<ProgressBar
						height={20}
						progressBarBackground={progressBarBackground}
						progressBackgroundWidth={(width - padding * 2) * 0.35}
						progressWidth={
							(width - padding * 2) *
							0.35 *
							(todayGoal.achieved / todayGoal.goal)
						}
					/>
				</FlexWidget>
			</FlexWidget>
		);
	}

	if (cellsWidth < 3 && cellsHeight === 2) {
		return (
			<FlexWidget
				style={{
					height: "match_parent",
					width: "match_parent",
					justifyContent: "space-between",
					flexDirection: "column",
					backgroundColor: `${backgroundColor}${mainTransparency}`,
					borderRadius: 16,
					padding,
				}}
			>
				<TextWidget
					text="Hours"
					style={{
						color: red,
						fontSize: 25,
						fontWeight: "bold",
					}}
				/>

				<FlexWidget
					style={{
						flexDirection: "column",
						width: "match_parent",
					}}
				>
					<TextWidget
						text={
							ms(todayGoal.achieved * 1000, {
								unitLimit: 2,
								useAbbreviations: true,
							}) || `${todayGoal.achieved}s`
						}
						style={{
							color: textColor,
							fontSize: 32,
							fontWeight: "bold",
						}}
					/>

					<TextWidget
						text={`/${
							ms(todayGoal.goal * 1000, {
								unitLimit: 2,
								useAbbreviations: true,
							}) || `${todayGoal.goal}s`
						}`}
						style={{
							color: secondaryTextColor,
							fontSize: 18,
							marginBottom: 5,
						}}
					/>

					<ProgressBar
						height={20}
						progressBackgroundWidth="match_parent"
						progressBarBackground={progressBarBackground}
						progressWidth={
							(width - padding * 2) * (todayGoal.achieved / todayGoal.goal)
						}
					/>
				</FlexWidget>
			</FlexWidget>
		);
	}

	if (cellsWidth === 3 && cellsHeight >= 2) {
		padding = 12;

		const svg = createCircularProgressBar(
			(todayGoal.achieved / todayGoal.goal) * 100,
			{
				backgroundColor: progressBarBackground,
				barWidth: 30,
				fontSize: 60,
				progressColor: red,
				showPercentage: true,
				size: width * 1.6,
				textColor,
			},
		);

		return (
			<FlexWidget
				style={{
					height: "match_parent",
					width: "match_parent",
					flexDirection: "column",
					backgroundColor: `${backgroundColor}${mainTransparency}`,
					borderRadius: 16,
					padding,
				}}
			>
				<FlexWidget
					style={{
						width: "match_parent",
						height: height * 0.15,
						flexDirection: "row",
						justifyContent: "space-between",
					}}
				>
					<TextWidget
						text="Hours"
						style={{
							color: red,
							fontSize: 25,
							fontWeight: "bold",
						}}
					/>

					<FlexWidget
						style={{
							flexDirection: "row",
							// width: "match_parent",
						}}
					>
						<TextWidget
							text={
								ms(todayGoal.achieved * 1000, {
									unitLimit: 2,
									useAbbreviations: true,
								}) || `${todayGoal.achieved}s`
							}
							style={{
								color: textColor,
								fontSize: 26,
								fontWeight: "bold",
							}}
						/>

						<TextWidget
							text={` /${
								ms(todayGoal.goal * 1000, {
									unitLimit: 2,
									useAbbreviations: true,
								}) || `${todayGoal.goal}s`
							}`}
							style={{
								color: secondaryTextColor,
								fontSize: 18,
								marginBottom: 5,
							}}
						/>
					</FlexWidget>
				</FlexWidget>

				<FlexWidget
					style={{
						width: "match_parent",
						height: height * 0.8,
						justifyContent: "center",
						alignItems: "center",
					}}
				>
					<SvgWidget svg={svg} />
				</FlexWidget>
			</FlexWidget>
		);
	}

	if (cellsWidth > 3 && cellsHeight >= 2) {
		const past7DaysHeight = height * 0.8;

		const endDate = new Date();
		const startDate = add(endDate, {
			weeks: -1,
		});

		startDate.setUTCHours(0, 0, 0, 0);
		endDate.setUTCHours(0, 0, 0, 0);

		const dates = getDatesBetween(startDate, endDate);

		return (
			<FlexWidget
				style={{
					height: "match_parent",
					width: "match_parent",
					flexDirection: "column",
					justifyContent: "space-between",
					backgroundColor: `${backgroundColor}${mainTransparency}`,
					borderRadius: 16,
					padding,
				}}
			>
				<FlexWidget
					style={{
						flexDirection: "row",
						height: height * 0.8,
						width: "match_parent",
					}}
				>
					<FlexWidget
						style={{
							height: "match_parent",
							width: width * 0.25,
							flexDirection: "column",
							justifyContent: "space-between",
						}}
					>
						<TextWidget
							text="Hours"
							style={{
								color: red,
								fontSize: 25,
								fontWeight: "bold",
							}}
						/>

						<FlexWidget>
							<TextWidget
								text={
									ms(todayGoal.achieved * 1000, {
										unitLimit: 2,
										useAbbreviations: true,
									}) || `${todayGoal.achieved}s`
								}
								style={{
									color: textColor,
									fontSize: 20,
									fontWeight: "bold",
								}}
							/>

							<TextWidget
								text={`/${
									ms(todayGoal.goal * 1000, {
										unitLimit: 2,
										useAbbreviations: true,
									}) || `${todayGoal.goal}s`
								}`}
								style={{
									color: secondaryTextColor,
									fontSize: 14,
									marginBottom: 5,
								}}
							/>
						</FlexWidget>
					</FlexWidget>

					<FlexWidget
						style={{
							height: "match_parent",
							width: width * 0.7,
							flexDirection: "row",
							flexGap: 12,
							justifyContent: "flex-end",
							paddingRight: 8,
						}}
					>
						{dates
							.map((date) => {
								const goal = data.goals.find(
									(goal) =>
										formatDate(new Date(goal.date)) === formatDate(date),
								);

								if (goal) return goal;

								return {
									date: date.toISOString(),
									achieved: 0,
									goal: 1,
								};
							})
							.map((goal) => {
								const goalDate = new Date(goal.date);

								const dayNumber = goalDate.getUTCDate().toString();
								const todayDayNumber = today.getUTCDate().toString();

								const isToday = dayNumber === todayDayNumber;
								const isSunday = goalDate.getUTCDay() === 0;

								return (
									<FlexWidget key={goal.date}>
										<VerticalProgressBar
											width={10}
											progressBackgroundHeight={past7DaysHeight - padding * 2}
											progressHeight={
												(past7DaysHeight - padding * 2) *
												(goal.achieved / goal.goal)
											}
											progressBarBackground={
												goal.goal === 1
													? progressBarDisabledBackground
													: progressBarBackground
											}
										/>

										<TextWidget
											text={dayNumber}
											style={{
												fontWeight: isToday ? "bold" : "normal",
												color: isSunday ? red : secondaryTextColor,
											}}
										/>
									</FlexWidget>
								);
							})}
					</FlexWidget>
				</FlexWidget>

				<FlexWidget
					style={{
						flexDirection: "row",
						height: height * 0.2,
						width: "match_parent",
					}}
				>
					<ProgressBar
						height={30}
						progressBackgroundWidth="match_parent"
						progressBarBackground={progressBarBackground}
						progressWidth={
							(width - padding * 2) * (todayGoal.achieved / todayGoal.goal)
						}
					/>
				</FlexWidget>
			</FlexWidget>
		);
	}

	// cellsWidth <= 3 && cellsHeight === 1
	return (
		<FlexWidget
			style={{
				height: "match_parent",
				width: "match_parent",
				flexDirection: "column",
				justifyContent: "space-between",
				backgroundColor: `${backgroundColor}${mainTransparency}`,
				borderRadius: 16,
				padding,
			}}
		>
			<FlexWidget
				style={{
					justifyContent: "center",
					alignItems: "center",
					width: "match_parent",
				}}
			>
				<TextWidget
					text={
						ms(todayGoal.achieved * 1000, {
							unitLimit: 2,
							useAbbreviations: true,
						}) || `${todayGoal.achieved}s`
					}
					style={{
						textAlign: "center",
						width: "match_parent",
						color: textColor,
						fontSize: 32,
						fontWeight: "bold",
						marginTop: 10,
					}}
				/>
			</FlexWidget>

			<ProgressBar
				height={20}
				progressBackgroundWidth={"match_parent"}
				progressBarBackground={progressBarBackground}
				progressWidth={
					(width - padding * 2) * (todayGoal.achieved / todayGoal.goal)
				}
			/>
		</FlexWidget>
	);
}

interface ProgressBarProps {
	progressBarBackground: ColorProp;
	progressBackgroundWidth: number | "wrap_content" | "match_parent";
	progressWidth: number;
	height: number;
}

function ProgressBar({
	progressBackgroundWidth,
	progressBarBackground,
	progressWidth,
	height,
}: ProgressBarProps) {
	"use no memo";

	return (
		<FlexWidget
			style={{
				width: progressBackgroundWidth,
				height,
				backgroundColor: progressBarBackground,
				borderRadius: 16,
				overflow: "hidden",
			}}
		>
			<FlexWidget
				style={{
					width: progressWidth,
					backgroundColor: red,
					height: "match_parent",
					borderRadius: 16,
				}}
			></FlexWidget>
		</FlexWidget>
	);
}

interface VerticalProgressBarProps {
	progressBarBackground: ColorProp;
	progressBackgroundHeight: number | "wrap_content" | "match_parent";
	progressHeight: number;
	width: number;
}

function VerticalProgressBar({
	progressBackgroundHeight,
	progressBarBackground,
	progressHeight,
	width,
}: VerticalProgressBarProps) {
	"use no memo";

	return (
		<FlexWidget
			style={{
				width,
				height: progressBackgroundHeight,
				backgroundColor: progressBarBackground,
				borderRadius: 16,
				overflow: "hidden",
				justifyContent: "flex-end",
			}}
		>
			<FlexWidget
				style={{
					width: "match_parent",
					backgroundColor: red,
					height: progressHeight,
					borderRadius: 16,
				}}
			></FlexWidget>
		</FlexWidget>
	);
}

interface CircularProgressBarOptions {
	barWidth: number;
	backgroundColor: string;
	progressColor: string;
	size: number;
	showPercentage?: boolean;
	fontSize: number;
	textColor: string;
}

function createCircularProgressBar(
	progress: number,
	options: CircularProgressBarOptions,
): string {
	const clampedProgress = Math.max(0, Math.min(100, progress));

	const {
		barWidth,
		backgroundColor,
		progressColor,
		size,
		showPercentage = false,
		fontSize,
		textColor,
	} = options;

	const center = size / 2;
	const radius = (size - barWidth) / 2;
	const circumference = 2 * Math.PI * radius;
	const strokeDashoffset =
		circumference - (clampedProgress / 100) * circumference;

	return `<svg width="${size}" height="${size}" viewBox="0 0 ${size} ${size}" xmlns="http://www.w3.org/2000/svg">
		<circle
			cx="${center}"
			cy="${center}"
			r="${radius}"
			fill="none"
			stroke="${backgroundColor}"
			stroke-width="${barWidth}"
		/>

		<circle
			cx="${center}"
			cy="${center}"
			r="${radius}"
			fill="none"
			stroke="${progressColor}"
			stroke-width="${barWidth}"
			stroke-dasharray="${circumference}"
			stroke-dashoffset="${strokeDashoffset}"
			stroke-linecap="round"
		/>
		${
			showPercentage
				? `<text
					x="${center}"
					y="${center + fontSize / 4}"
					text-anchor="middle"
					dominant-baseline="middle"
					font-size="${fontSize}"
					font-family="Arial, sans-serif"
					fill="${textColor}"
				>${clampedProgress.toFixed(0)}%</text>`
				: ""
		}
	</svg>`;
}

export async function handleUpdate(): Promise<WidgetData> {
	const endDate = new Date();
	const startDate = add(new Date(), {
		weeks: -1,
	});

	const goals = await getUserGoals({
		startDate: formatDate(startDate),
		endDate: formatDate(endDate),
	});

	const transparency = Number.parseInt(db.get("Goal_trasparency") || "60", 10);
	const theme = (db.get("Goal_theme") as WidgetData["theme"]) || "dark";

	return {
		transparency,
		theme,
		goals,
	};
}

export const handleSetup = handleUpdate;
