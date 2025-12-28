import Button from "@/components/Button";
import Header from "@/components/Header";
import MaterialSymbols from "@/components/MaterialSymbols";
import Skeleton from "@/components/Skeleton";
import Text from "@/components/Text";
import { slate } from "@/constants/hcColors";
import { getCurrentUserProjects } from "@/functions/hackatime";
import { styles } from "@/styles/projects";
import type { ProjectDetail } from "@/types/hackatime";
import ms from "enhanced-ms";
import { type ExternalPathString, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ScrollView, View } from "react-native";

export default function Projects() {
	const router = useRouter();

	const [projects, setProjects] = useState<ProjectDetail[] | null>(null);

	useEffect(() => {
		getCurrentUserProjects(true).then((res) => {
			setProjects(typeof res === "string" ? null : res.projects);
		});
	}, []);

	return (
		<View style={{ flex: 1 }}>
			<Header />

			<ScrollView>
				{Array.isArray(projects) ? (
					// biome-ignore lint/complexity/noUselessFragments: The fragment is required
					<>
						{projects.length === 0 ? (
							<Text style={styles.noProjectText}>No Projects Available</Text>
						) : (
							projects.map((project) => (
								<View key={project.name} style={styles.projectContainer}>
									<View style={styles.header}>
										<Text style={styles.projectName}>{project.name}</Text>

										<View>
											{project.repo_url && (
												<Button
													containerStyle={styles.buttonContainer}
													buttonStyle={styles.button}
													type="secondary"
													icon="folder_code"
													onPress={() => {
														router.push(project.repo_url as ExternalPathString);
													}}
												/>
											)}
										</View>
									</View>

									<Text style={styles.hoursText}>
										{ms(project.total_seconds * 1000, {
											useAbbreviations: true,
										}) || `${project.total_seconds}s`}
									</Text>

									<View style={styles.languagesContainer}>
										<MaterialSymbols name="code" size={20} color={slate} />

										<Text style={styles.languagesText}>
											{project.languages.join(", ")}
										</Text>
									</View>

									<View style={styles.lastHeartbeatContainer}>
										<MaterialSymbols name="timeline" size={20} color={slate} />

										<Text style={styles.lastHeartbeatText}>
											Last heartbeat{" "}
											{ms(
												Date.now() - new Date(project.last_heartbeat).getTime(),
												{
													useAbbreviations: true,
													unitLimit: 1,
												},
											)}{" "}
											ago
										</Text>
									</View>
								</View>
							))
						)}
					</>
				) : (
					// biome-ignore lint/complexity/noUselessFragments: The fragment is required
					<>
						{new Array(5).fill(0).map((_, i) => (
							// biome-ignore lint/suspicious/noArrayIndexKey: the index is the only non-duplicate key
							<View key={i} style={styles.skeletonProjectContainer}>
								<Skeleton width="100%" height={160} radius={10} />
							</View>
						))}
					</>
				)}
			</ScrollView>
		</View>
	);
}
