import Button from "@/components/Button";
import DatePicker from "@/components/DatePicker";
import Header from "@/components/Header";
import Popup from "@/components/Popup";
import ProgressBar from "@/components/ProgressBar";
import Sidebar from "@/components/Sidebar";
import Text from "@/components/Text";
import TextInput from "@/components/TextInput";
import { getUser, getUserGoals, updateUserGoal } from "@/functions/server";
import { formatDate } from "@/functions/util";
import NoApiKeyOnServerPage from "@/pages/noApiKeyOnServer";
import { styles } from "@/styles/goals";
import type { Goal } from "@/types/server";
import { add } from "date-fns/add";
import ms from "enhanced-ms";
import { Skeleton } from "moti/skeleton";
import { useEffect, useState } from "react";
import { ScrollView, ToastAndroid, View } from "react-native";
import type { DateType } from "react-native-ui-datepicker";

export default function Goals() {
	const [isApiKeyOnServer, setIsApiKeyOnServer] = useState<boolean>(true);

	const [goals, setGoals] = useState<Goal[] | null>(null);

	const [datePickerOpen, setDatePickerOpen] = useState(false);

	const [statsRange, setStatsRange] = useState<"alltime" | "custom">("custom");
	const [range, setRange] = useState<{
		startDate: DateType;
		endDate: DateType;
	}>({
		startDate: add(new Date(), {
			weeks: -1,
		}),
		endDate: new Date(),
	});

	const [statsRangeName, setStatsRangeName] = useState<string>("");

	const [changeGoalPopupOpen, setChangeGoalPopupOpen] = useState(false);
	const [newGoal, setNewGoal] = useState<string>("");

	useEffect(() => {
		getUser().then(setIsApiKeyOnServer);
	}, []);

	useEffect(() => {
		setGoals(null);

		if (statsRange === "alltime") {
			setStatsRangeName("All Time");

			getUserGoals({
				all: true,
			}).then((goals) => {
				setGoals(goals);
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

					getUserGoals({
						startDate: formattedStartDate,
						endDate: newFormattedEndDate,
					}).then((goals) => {
						setGoals(goals);
					});

					return;
				}

				setStatsRangeName(`${formattedStartDate} - ${formattedEndDate}`);

				getUserGoals({
					startDate: formattedStartDate,
					endDate: formattedEndDate,
				}).then((goals) => {
					setGoals(goals);
				});

				return;
			}

			if (!range.startDate && range.endDate) {
				const startDate = new Date(range.endDate as string | Date | number);
				const endDate = add(startDate, { days: 1 });

				const formattedStartDate = formatDate(startDate);
				const formattedEndDate = formatDate(endDate);

				setStatsRangeName(`${formattedStartDate} - ${formattedEndDate}`);

				getUserGoals({
					startDate: formattedStartDate,
					endDate: formattedEndDate,
				}).then((goals) => {
					setGoals(goals);
				});

				return;
			}

			if (range.startDate && !range.endDate) {
				const startDate = new Date(range.startDate as string | Date | number);
				const endDate = add(startDate, { days: 1 });

				const formattedStartDate = formatDate(startDate);
				const formattedEndDate = formatDate(endDate);

				setStatsRangeName(`${formattedStartDate} - ${formattedEndDate}`);

				getUserGoals({
					startDate: formattedStartDate,
					endDate: formattedEndDate,
				}).then((goals) => {
					setGoals(goals);
				});

				return;
			}
		}
	}, [statsRange, range]);

	useEffect(() => {
		if (!isApiKeyOnServer) return;

		const endDate = new Date();
		const startDate = add(endDate, { weeks: -1 });

		getUserGoals({
			startDate: formatDate(startDate),
			endDate: formatDate(endDate),
		}).then((goals) => {
			setGoals(goals);
		});
	}, [isApiKeyOnServer]);

	return (
		<View style={{ flex: 1 }}>
			<Header />
			<Sidebar />

			<DatePicker
				open={datePickerOpen}
				onClose={() => {
					setDatePickerOpen(false);
				}}
				onChange={setRange}
				mode="range"
				showOutsideDays
				startDate={range.startDate}
				endDate={range.endDate}
				maxDate={new Date()}
			>
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

			<Popup
				hidden={!changeGoalPopupOpen}
				onClose={() => {
					setChangeGoalPopupOpen(false);
				}}
			>
				<View>
					<Text style={styles.updateGoalTitle}>Update Goal</Text>

					<TextInput
						placeholder="5h"
						title="New Goal"
						onValueChange={(value) => {
							setNewGoal(value);
						}}
					/>

					<Button
						text="Save"
						type="primary"
						containerStyle={styles.updateGoalSaveButton}
						onPress={async () => {
							const today = new Date();
							today.setUTCHours(0, 0, 0, 0);

							const formattedToday = formatDate(today);

							const newGoalMs = ms(newGoal);

							if (newGoalMs < 60 * 1000 || newGoalMs > 60 * 60 * 23 * 1000)
								return ToastAndroid.show(
									"Goal must be between 1 minute and 23 hours",
									ToastAndroid.LONG,
								);

							const success = updateUserGoal(
								formattedToday,
								Math.round(newGoalMs / 1000),
							);

							setChangeGoalPopupOpen(false);

							if (!success)
								return ToastAndroid.show(
									"Failed to update goal",
									ToastAndroid.LONG,
								);

							if (statsRange === "alltime") {
								const newGoals = await getUserGoals({
									all: true,
								});

								setGoals(newGoals);
							} else {
								const newGoals = await getUserGoals({
									startDate: formatDate(
										new Date(range.startDate as string | Date | number),
									),
									endDate: formatDate(
										new Date(range.endDate as string | Date | number),
									),
								});

								setGoals(newGoals);
							}

							ToastAndroid.show("Goal updated", ToastAndroid.SHORT);
						}}
					/>
				</View>
			</Popup>

			{!isApiKeyOnServer && <NoApiKeyOnServerPage />}

			<Button
				containerStyle={styles.rangeButton}
				type="outline"
				text={`Range: ${statsRangeName}`}
				onPress={() => {
					setDatePickerOpen(true);
				}}
			/>

			<Button
				type="outline"
				text="Update Goal"
				containerStyle={styles.updateGoalButton}
				onPress={() => {
					setChangeGoalPopupOpen(true);
				}}
			/>

			<ScrollView style={styles.goalsContainer}>
				{Array.isArray(goals) ? (
					goals.length === 0 ? (
						<View style={styles.noGoalsContainer}>
							<Text style={styles.noGoalsText}>
								You don't have any goal yet
							</Text>
						</View>
					) : (
						goals.map((goal) => (
							<View key={goal.date} style={styles.goalContainer}>
								<View style={styles.header}>
									<Text style={styles.goalDate}>
										{formatDate(new Date(goal.date))}
									</Text>

									<Text style={styles.goalGoal}>
										{goal.goal < 60
											? `${goal.goal}s`
											: ms(goal.goal * 1000, {
													useAbbreviations: true,
													unitLimit: 2,
												})}
									</Text>
								</View>

								<View style={styles.headerFooterDivider} />

								<View style={styles.progressHeader}>
									<Text style={styles.progressTitle}>
										{goal.achieved < 60
											? `${goal.achieved}s`
											: ms(goal.achieved * 1000, {
													useAbbreviations: true,
													unitLimit: 2,
												})}{" "}
										/{" "}
										{goal.goal < 60
											? `${goal.goal}s`
											: ms(goal.goal * 1000, {
													useAbbreviations: true,
													unitLimit: 2,
												})}
									</Text>

									<Text style={styles.progressPercentage}>
										{goal.goal === 0
											? 100
											: ((goal.achieved / goal.goal) * 100).toFixed(1)}
										%
									</Text>
								</View>

								<ProgressBar
									height={10}
									progress={
										goal.goal === 0 ? 100 : (goal.achieved / goal.goal) * 100
									}
									style={styles.progressBar}
								/>
							</View>
						))
					)
				) : (
					new Array(5).fill(0).map((_, i) => (
						// biome-ignore lint/suspicious/noArrayIndexKey: the index is the only non-duplicate key
						<View key={i} style={styles.skeletonGoalContainer}>
							<Skeleton width="100%" height={160} radius={10} />
						</View>
					))
				)}
			</ScrollView>
		</View>
	);
}
