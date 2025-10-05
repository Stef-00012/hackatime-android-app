import { background, elevated, red, smoke, text, white } from "@/constants/hcColors";
import * as db from "@/functions/database";
import { getCurrentUserStatsLast7Days } from "@/functions/hackatime";
import { getTop } from "@/functions/util";
import {
    FlexWidget,
    TextWidget,
    type WidgetInfo
} from "react-native-android-widget";

export { ConfigurationScreen } from "./ConfigurationScreen";

interface WidgetData {
	transparency: number;
	theme: "light" | "dark";
	topProject: string;
	topLanguage: string;
	topEditor: string;
	topOS: string;
}

export interface Props {
	data: WidgetData;
	widgetInfo?: WidgetInfo;
}

export function Widget({ data, widgetInfo }: Props) {
	"use no memo";

	const mainTransparencyNumber = Math.round((data.transparency / 100) * 255);
	const mainTransparency = mainTransparencyNumber.toString(16).padStart(2, "0");

	const boxTransparencyNumber = Math.min(mainTransparencyNumber + 80, 255);
	const boxTransparency = boxTransparencyNumber.toString(16).padStart(2, "0");

	const backgroundColor = data.theme === "dark" ? background : white;
    const boxBackground = data.theme === "dark" ? elevated : smoke;
	const textColor = data.theme === "dark" ? text : background;

    // const height = widgetInfo?.height || 999;
    // const width = widgetInfo?.width || 999;
    const height = widgetInfo?.height || 115;
    const width = widgetInfo?.width || 170;

    const showTopLanguage = width > 300;
    const showSecondRow = height > 115;

    const showTopOS = showSecondRow && width > 300;

    const topProjectMaxLength = showTopLanguage
        ? Math.floor(((width / 2) - 32) / 12)
        : Math.floor((width - 32) / 12);

    const topLanguageMaxLength = Math.floor(((width / 2) - 32) / 12);

    const topEditorMaxLength = showTopOS
        ? Math.floor(((width / 2) - 32) / 12)
        : Math.floor((width - 32) / 12);

    const topOSMaxLength = Math.floor(((width / 2) - 32) / 12);

	return (
		<FlexWidget
			style={{
				height: "match_parent",
				width: "match_parent",
				flexDirection: "column",
				backgroundColor: `${backgroundColor}${mainTransparency}`,
				borderRadius: 16,
			}}
		>
            <FlexWidget
                style={{
                    flexDirection: "row",
                }}
            >
                <FlexWidget style={{
                    backgroundColor: `${boxBackground}${boxTransparency}`,
                    flex: 1,
                    width: (showTopLanguage ? width / 2 : width) - 16,
                    height: (showSecondRow ? height / 2 : height) - 16,
                    margin: 8,
                    borderRadius: 16,
                    padding: 8,
                }}>
                    <TextWidget
                        text="Top Project"
                        style={{
                            color: red,
                            fontWeight: "bold",
                            fontSize: 20,
                        }}
                    />

                    <TextWidget
                        text={data.topProject.length > topProjectMaxLength ? `${data.topProject.slice(0, topProjectMaxLength)}…` : data.topProject}
                        style={{
                            color: textColor,
                            fontSize: 20,
                            marginTop: 13,
                        }}
                    />
                </FlexWidget>

                {showTopLanguage && (
                    <FlexWidget style={{
                        backgroundColor: `${boxBackground}${boxTransparency}`,
                        flex: 1,
                        width: (width / 2) - 16,
                        height: (showSecondRow ? height / 2 : height) - 16,
                        margin: 8,
                        borderRadius: 16,
                        padding: 8,
                    }}>
                        <TextWidget
                            text="Top Language"
                            style={{
                                color: red,
                                fontWeight: "bold",
                                fontSize: 20,
                            }}
                        />

                        <TextWidget
                            text={data.topLanguage.length > topLanguageMaxLength ? `${data.topLanguage.slice(0, topLanguageMaxLength)}…` : data.topLanguage}
                            style={{
                                color: textColor,
                                fontSize: 20,
                                marginTop: 13
                            }}
                        />
                    </FlexWidget>
                )}
            </FlexWidget>

            {showSecondRow && (
                <FlexWidget
                    style={{
                        flexDirection: "row",
                    }}
                >
                    <FlexWidget style={{
                        backgroundColor: `${boxBackground}${boxTransparency}`,
                        flex: 1,
                        width: (showTopLanguage ? width / 2 : width) - 16,
                        height: (height / 2) - 16,
                        margin: 8,
                        borderRadius: 16,
                        padding: 8,
                    }}>
                        <TextWidget
                            text="Top Editor"
                            style={{
                                color: red,
                                fontWeight: "bold",
                                fontSize: 20,
                            }}
                        />

                        <TextWidget
                            text={data.topEditor.length > topEditorMaxLength ? `${data.topEditor.slice(0, topEditorMaxLength)}…` : data.topEditor}
                            style={{
                                color: textColor,
                                fontSize: 20,
                                marginTop: 13
                            }}
                        />
                    </FlexWidget>

                    {showTopOS && (
                        <FlexWidget style={{
                            backgroundColor: `${boxBackground}${boxTransparency}`,
                            flex: 1,
                            width: (width / 2) - 16,
                            height: (height / 2) - 16,
                            margin: 8,
                            borderRadius: 16,
                            padding: 8,
                        }}>
                            <TextWidget
                                text="Top OS"
                                style={{
                                    color: red,
                                    fontWeight: "bold",
                                    fontSize: 20,
                                }}
                            />

                            <TextWidget
                                text={data.topOS.length > topOSMaxLength ? `${data.topOS.slice(0, topOSMaxLength)}…` : data.topOS}
                                style={{
                                    color: textColor,
                                    fontSize: 20,
                                    marginTop: 13
                                }}
                            />
                        </FlexWidget>
                    )}
                </FlexWidget>
            )}
		</FlexWidget>
	);
}

export async function handleUpdate(): Promise<WidgetData> {
    const last7DaysStats = await getCurrentUserStatsLast7Days({
        features: [
            "projects",
            "languages",
            "editors",
            "operating_systems",
        ]
    });

    const transparency = Number.parseInt(db.get("TopStats_trasparency") || "60", 10);
    const theme = (db.get("TopStats_theme") as WidgetData["theme"]) || "dark";

    if (typeof last7DaysStats === "string") {
        return {
            transparency,
            theme,
            topEditor: "N/A",
            topLanguage: "N/A",
            topOS: "N/A",
            topProject: "N/A",
        }
    }

    const topEditor = (getTop(last7DaysStats.editors)?.name) || "N/A";
    const topLanguage = (getTop(last7DaysStats.languages)?.name) || "N/A";
    const topOS = (getTop(last7DaysStats.operating_systems)?.name) || "N/A";
    const topProject = (getTop(last7DaysStats.projects)?.name) || "N/A";

    return {
        transparency,
        theme,
        topEditor,
        topLanguage,
        topOS,
        topProject,
    }
}

export const handleSetup = handleUpdate;