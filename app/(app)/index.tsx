import Button from "@/components/Button";
import ChartLegend from "@/components/ChartLegend";
import DatePicker from "@/components/DatePicker";
import Header from "@/components/Header";
import Sidebar from "@/components/Sidebar";
import Skeleton from "@/components/Skeleton";
import Text from "@/components/Text";
import { elevated, red, slate } from "@/constants/hcColors";
import { languageColors } from "@/constants/languageColors";
import { AuthContext } from "@/contexts/AuthProvider";
import {
	getCurrentUserStats,
	getCurrentUserStatsLast7Days,
} from "@/functions/hackatime";
import { getLast7DaysData } from "@/functions/hackatime-util";
import { colorHash, formatDate, getTop } from "@/functions/util";
import { lineChartWidth, styles } from "@/styles/home";
import type {
	UserStatsLast7DaysResponse,
	UserStatsResponse,
} from "@/types/hackatime";
import { add } from "date-fns/add";
import ms from "enhanced-ms";
import { useContext, useEffect, useState } from "react";
import { ScrollView, View } from "react-native";
import { CurveType, LineChart, PieChart } from "react-native-gifted-charts";
import type { DateType } from "react-native-ui-datepicker";

export default function Index() {
	const { isAuthenticating } = useContext(AuthContext);

	const [stats, setStats] = useState<
		UserStatsLast7DaysResponse["data"] | UserStatsResponse["data"] | null
	>(null);
	const [last7DaysData, setLast7DaysData] = useState<Awaited<
		ReturnType<typeof getLast7DaysData>
	> | null>(null);
	const [statsRange, setStatsRange] = useState<"7d" | "alltime" | "custom">(
		"7d",
	);
	const [statsRangeName, setStatsRangeName] = useState<string>("Last 7 Days");

	const [range, setRange] = useState<{
		startDate: DateType;
		endDate: DateType;
	}>({
		startDate: add(new Date(), {
			weeks: -1,
		}),
		endDate: new Date(),
	});

	const [datePickerOpen, setDatePickerOpen] = useState(false);

	const [topProject, setTopProject] = useState<string | null>(null);
	const [topLanguage, setTopLanguage] = useState<string | null>(null);
	const [topOS, setTopOS] = useState<string | null>(null);
	const [topEditor, setTopEditor] = useState<string | null>(null);
	const [topMachine, setTopMachine] = useState<string | null>(null);

	const [totalTime, setTotalTime] = useState<string | null>(null);

	useEffect(() => {
		getCurrentUserStatsLast7Days({
			features: [
				"projects",
				"languages",
				"operating_systems",
				"editors",
				"machines",
			],
		}).then((userStats) => {
			setStats(typeof userStats === "string" ? null : userStats);
		});

		getLast7DaysData().then((data) => {
			setLast7DaysData(data);
		});
	}, []);

	useEffect(() => {
		if (stats) {
			const topProject = getTop(stats.projects ?? [])?.name;
			const topLanguage = getTop(stats.languages ?? [])?.name;

			if ("operating_systems" in stats) {
				const topOS = getTop(stats.operating_systems ?? [])?.name;

				setTopOS(topOS || "N/A");
			} else {
				setTopOS("N/A");
			}

			if ("editors" in stats) {
				const topEditor = getTop(stats.editors ?? [])?.name;
				setTopEditor(topEditor || "N/A");
			} else {
				setTopEditor("N/A");
			}

			if ("machines" in stats) {
				const topMachine = getTop(stats.machines ?? [])?.name;
				setTopMachine(topMachine || "N/A");
			} else {
				setTopMachine("N/A");
			}

			if (statsRange === "7d" || statsRange === "alltime") {
				setTotalTime(
					ms(stats.total_seconds * 1000, {
						useAbbreviations: true,
					}) || `${stats.total_seconds}s`,
				);
			} else {
				const totalSeconds = Array.isArray(stats.languages)
					? stats.languages.reduce(
							(seconds, language) => seconds + language.total_seconds,
							0,
						)
					: 0;

				setTotalTime(
					ms(totalSeconds * 1000, {
						useAbbreviations: true,
					}) || `${totalSeconds}s`,
				);
			}

			setTopProject(topProject || "N/A");
			setTopLanguage(topLanguage || "N/A");
		}
	}, [stats, statsRange]);

	useEffect(() => {
		setTopEditor(null);
		setTopOS(null);
		setTopProject(null);
		setTopLanguage(null);
		setTotalTime(null);
		setTopMachine(null);

		if (statsRange === "7d" || (!range.startDate && !range.endDate)) {
			setStatsRangeName("Last 7 Days");

			getCurrentUserStatsLast7Days({
				features: [
					"projects",
					"languages",
					"operating_systems",
					"editors",
					"machines",
				],
			}).then((userStats) => {
				setStats(typeof userStats === "string" ? null : userStats);
			});

			getLast7DaysData().then((data) => {
				setLast7DaysData(data);
			});

			return;
		}

		if (statsRange === "alltime") {
			setStatsRangeName("All Time");

			getCurrentUserStats({
				features: ["projects", "languages"],
			}).then((userStats) => {
				setStats(typeof userStats === "string" ? null : userStats);
			});

			return;
		}

		if (statsRange === "custom") {
			if (range.startDate && range.endDate) {
				const startDate = new Date(range.startDate as string | Date | number);
				const endDate = new Date(range.endDate as string | Date | number);

				const formattedStartDate = formatDate(startDate);
				const formattedEndDate = formatDate(endDate);

				if (formattedStartDate === formattedEndDate) {
					const newEndDate = add(startDate, { days: 1 });
					const newFormattedEndDate = formatDate(newEndDate);

					setStatsRangeName(`${formattedStartDate} - ${newFormattedEndDate}`);

					getCurrentUserStats({
						features: ["projects", "languages"],
						startDate: formattedStartDate,
						endDate: formattedEndDate,
					}).then((userStats) => {
						setStats(typeof userStats === "string" ? null : userStats);
					});

					return;
				}

				setStatsRangeName(`${formattedStartDate} - ${formattedEndDate}`);

				getCurrentUserStats({
					features: ["projects", "languages"],
					startDate: formattedStartDate,
					endDate: formattedEndDate,
				}).then((userStats) => {
					setStats(typeof userStats === "string" ? null : userStats);
				});

				return;
			}

			if (!range.startDate && range.endDate) {
				const startDate = new Date(range.endDate as string | Date | number);
				const endDate = add(startDate, { days: 1 });

				const formattedStartDate = formatDate(startDate);
				const formattedEndDate = formatDate(endDate);

				setStatsRangeName(`${formattedStartDate} - ${formattedEndDate}`);

				getCurrentUserStats({
					features: ["projects", "languages"],
					startDate: formattedStartDate,
					endDate: formattedEndDate,
				}).then((userStats) => {
					setStats(typeof userStats === "string" ? null : userStats);
				});

				return;
			}

			if (range.startDate && !range.endDate) {
				const startDate = new Date(range.startDate as string | Date | number);
				const endDate = add(startDate, { days: 1 });

				const formattedStartDate = formatDate(startDate);
				const formattedEndDate = formatDate(endDate);

				setStatsRangeName(`${formattedStartDate} - ${formattedEndDate}`);

				getCurrentUserStats({
					features: ["projects", "languages"],
					startDate: formattedStartDate,
					endDate: formattedEndDate,
				}).then((userStats) => {
					setStats(typeof userStats === "string" ? null : userStats);
				});

				return;
			}
		}
	}, [statsRange, range]);

	return (
		<View style={{ flex: 1 }}>
			<DatePicker
				open={datePickerOpen}
				onClose={() => {
					setDatePickerOpen(false);

					if (!range.startDate && range.endDate) {
						setStatsRange("7d");
					}
				}}
				onChange={(params) => {
					setStatsRange("custom");

					setRange(params);
				}}
				mode="range"
				showOutsideDays
				startDate={range.startDate}
				endDate={range.endDate}
				maxDate={new Date()}
			>
				<Button
					text="Show Last 7 Days"
					type="primary"
					onPress={() => {
						setDatePickerOpen(false);
						setStatsRange("7d");

						setRange({
							startDate: add(new Date(), {
								weeks: -1,
							}),
							endDate: new Date(),
						});
					}}
					containerStyle={styles.datePickerButton}
				/>

				<Button
					text="Show All Time"
					type="primary"
					onPress={() => {
						setDatePickerOpen(false);
						setStatsRange("alltime");
					}}
					containerStyle={styles.datePickerShowAllTimeButton}
				/>
			</DatePicker>

			<View>
				<Header />
				<Sidebar />

				<ScrollView style={styles.mainContent}>
					<Button
						containerStyle={styles.rangeButton}
						type="outline"
						text={`Range: ${statsRangeName}`}
						onPress={() => {
							setDatePickerOpen(true);
						}}
					/>

					<View style={styles.statContainer}>
						<Text style={styles.subHeaderText}>Total Time</Text>

						<Skeleton width={160} height={40} radius="squircle">
							{isAuthenticating || !totalTime ? null : (
								<Text style={styles.statText}>{totalTime}</Text>
							)}
						</Skeleton>
					</View>

					<View style={styles.statContainer}>
						<Text style={styles.subHeaderText}>Top Project</Text>

						<Skeleton width={200} height={40} radius="squircle">
							{isAuthenticating || !topProject ? null : (
								<Text style={styles.statText}>{topProject}</Text>
							)}
						</Skeleton>
					</View>

					<View style={styles.statContainer}>
						<Text style={styles.subHeaderText}>Top Language</Text>

						<Skeleton width={130} height={40} radius="squircle">
							{isAuthenticating || !topLanguage ? null : (
								<Text style={styles.statText}>{topLanguage}</Text>
							)}
						</Skeleton>
					</View>

					{statsRange === "7d" && (
						<>
							<View style={styles.statContainer}>
								<Text style={styles.subHeaderText}>Top OS</Text>

								<Skeleton width={100} height={40} radius="squircle">
									{isAuthenticating || !topOS ? null : (
										<Text style={styles.statText}>{topOS}</Text>
									)}
								</Skeleton>
							</View>

							<View style={styles.statContainer}>
								<Text style={styles.subHeaderText}>Top Editor</Text>

								<Skeleton width={140} height={40} radius="squircle">
									{isAuthenticating || !topEditor ? null : (
										<Text style={styles.statText}>{topEditor}</Text>
									)}
								</Skeleton>
							</View>

							<View style={styles.statContainer}>
								<Text style={styles.subHeaderText}>Top Machine</Text>

								<Skeleton width={140} height={40} radius="squircle">
									{isAuthenticating || !topMachine ? null : (
										<Text style={styles.statText}>{topMachine}</Text>
									)}
								</Skeleton>
							</View>

							<View style={styles.chartContainer}>
								<Text style={styles.chartTitle}>Last 7 Days Overview</Text>

								<View style={styles.pieChartContainer}>
									<Skeleton width={"100%"} height={220} radius="squircle">
										{!isAuthenticating && last7DaysData ? (
											<LineChart
												data={Object.values(last7DaysData).map((dayData) => {
													const totalSeconds =
														typeof dayData.data === "string"
															? 0
															: Array.isArray(dayData.data.languages)
																? dayData.data.languages.reduce(
																		(seconds, language) =>
																			seconds + language.total_seconds,
																		0,
																	)
																: 0;

													const value = totalSeconds;

													const label = new Date(
														dayData.date,
													).toLocaleDateString("en-US", { weekday: "short" });

													// const dataPointText =
													// 	ms(Number(totalSeconds) * 1000, {
													// 		useAbbreviations: true,
													// 		unitLimit: 1,
													// 	}) || "0s";

													return {
														value,
														label,
														dataPointColor: red,
														// dataPointText,
													};
												})}
												formatYLabel={(value) =>
													ms(Number(value) * 1000, {
														useAbbreviations: true,
														unitLimit: 1,
													}) || "0s"
												}
												width={lineChartWidth}
												curved
												backgroundColor={elevated}
												color={red}
												curveType={CurveType.QUADRATIC}
												xAxisColor={slate}
												yAxisColor={slate}
												yAxisTextStyle={styles.lineChartAxisText}
												xAxisLabelTextStyle={[
													styles.lineChartAxisText,
													styles.lineChartXAxisText,
												]}
												yAxisLabelWidth={40}
												rulesColor={slate}
												disableScroll
												initialSpacing={3}
												endSpacing={0}
												spacing={lineChartWidth / 6 - 1}
											/>
										) : null}
									</Skeleton>
								</View>
							</View>
						</>
					)}

					<View style={styles.chartContainer}>
						<Text style={styles.chartTitle}>Languages</Text>

						<View style={styles.pieChartContainer}>
							<Skeleton width={250} height={250} radius="round">
								{!isAuthenticating &&
								stats?.languages &&
								stats.languages?.length > 0 ? (
									<PieChart
										data={stats.languages.map((language) => ({
											value: language.total_seconds,
											color:
												languageColors[
													language.name.toLowerCase() as keyof typeof languageColors
												] || colorHash(language.name),
										}))}
									/>
								) : null}
							</Skeleton>
						</View>

						<ChartLegend
							data={
								stats?.languages?.map((language) => ({
									label: language.name,
									color:
										languageColors[
											language.name.toLowerCase() as keyof typeof languageColors
										] || colorHash(language.name), // TODO: Use language main color instead of hash
								})) || []
							}
						/>
					</View>

					{statsRange === "7d" && (
						<>
							{stats && "editors" in stats && (
								<View style={styles.chartContainer}>
									<Text style={styles.chartTitle}>Editors</Text>

									<View style={styles.pieChartContainer}>
										<Skeleton width={250} height={250} radius="round">
											{!isAuthenticating &&
											stats.editors &&
											stats.editors.length > 0 ? (
												<PieChart
													data={stats.editors.map((editor) => ({
														value: editor.total_seconds,
														color: colorHash(editor.name),
													}))}
												/>
											) : null}
										</Skeleton>
									</View>

									<ChartLegend
										data={
											stats.editors.map((editor) => ({
												label: editor.name,
												color: colorHash(editor.name),
											})) || []
										}
									/>
								</View>
							)}

							{stats && "operating_systems" in stats && (
								<View style={styles.chartContainer}>
									<Text style={styles.chartTitle}>Operating Systems</Text>

									<View style={styles.pieChartContainer}>
										<Skeleton width={250} height={250} radius="round">
											{!isAuthenticating &&
											stats.operating_systems &&
											stats.operating_systems.length > 0 ? (
												<PieChart
													data={stats.operating_systems.map((os) => ({
														value: os.total_seconds,
														color: colorHash(os.name),
													}))}
												/>
											) : null}
										</Skeleton>
									</View>

									<ChartLegend
										data={stats.operating_systems.map((os) => ({
											label: os.name,
											color: colorHash(os.name),
										}))}
									/>
								</View>
							)}

							{stats && "machines" in stats && (
								<View style={styles.chartContainer}>
									<Text style={styles.chartTitle}>Machines</Text>

									<View style={styles.pieChartContainer}>
										<Skeleton width={250} height={250} radius="round">
											{!isAuthenticating &&
											stats.machines &&
											stats.machines.length > 0 ? (
												<PieChart
													data={stats.machines.map((machine) => ({
														value: machine.total_seconds,
														color: colorHash(machine.name),
													}))}
												/>
											) : null}
										</Skeleton>
									</View>

									<ChartLegend
										data={stats?.machines?.map((machine) => ({
											label: machine.name,
											color: colorHash(machine.name),
										}))}
									/>
								</View>
							)}
						</>
					)}
				</ScrollView>
			</View>
		</View>
	);
}
