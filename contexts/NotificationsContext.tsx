import { getCurrentUserTodayData } from "@/functions/hackatime";
import * as BackgroundTask from 'expo-background-task';
import * as Notifications from "expo-notifications";
import * as TaskManager from "expo-task-manager";
import { createContext, type ReactNode, useEffect } from "react";

interface Props {
	children: ReactNode;
}

export const NotificationsContext = createContext(null);

export const MOTIVATIONAL_QUOTES_TASK = "motivational-quotes";

TaskManager.defineTask(MOTIVATIONAL_QUOTES_TASK, async () => {
    console.log("Background task started");
	const todayData = await getCurrentUserTodayData();
    console.log("Background task executed", todayData);

    // if (typeof todayData === "string") return BackgroundTask.BackgroundTaskResult.Failed;

    // if (todayData.grand_total.total_seconds > 20 * 60) return BackgroundTask.BackgroundTaskResult.Failed;

    await Notifications.scheduleNotificationAsync({
        content: {
            title: typeof todayData === "string" ? todayData : todayData.grand_total.text,
        },
        trigger: null,
    });

    return BackgroundTask.BackgroundTaskResult.Success;
});

let resolver: (() => void) | null;

const promise = new Promise<void>((resolve) => {
    resolver = resolve;
});

Notifications.setNotificationChannelAsync("motivational-quotes", {
    name: "Motivational Quotes",
    importance: Notifications.AndroidImportance.DEFAULT,
    description:
        "Receive daily hackclub-themed motivational quotes to encourage you to code",
});

Notifications.setNotificationHandler({
    handleNotification: async () => ({
        shouldPlaySound: false,
        shouldSetBadge: false,
        shouldShowBanner: true,
        shouldShowList: true,
    }),
});

Notifications.getPermissionsAsync().then(async (status) => {
    await promise;

    let finalStatus = status.status;

    if (finalStatus === Notifications.PermissionStatus.DENIED) return;

    if (finalStatus === Notifications.PermissionStatus.UNDETERMINED) {
        const { status } = await Notifications.requestPermissionsAsync();

        finalStatus = status;
    }

    if (finalStatus !== Notifications.PermissionStatus.GRANTED) return;

    const backgroundTaskStatus = await BackgroundTask.getStatusAsync();

    if (backgroundTaskStatus !== BackgroundTask.BackgroundTaskStatus.Available) return;

    const isTaskRegistered = await TaskManager.isTaskRegisteredAsync(MOTIVATIONAL_QUOTES_TASK);

    console.log({ isTaskRegistered });

    if (isTaskRegistered) {
        console.log("Unregistering old task");
        // Notifications.unregisterTaskAsync(MOTIVATIONAL_QUOTES_TASK);
        await BackgroundTask.unregisterTaskAsync(MOTIVATIONAL_QUOTES_TASK)
    }

    console.log("Registering background task");
    
    await BackgroundTask.registerTaskAsync(MOTIVATIONAL_QUOTES_TASK, {
        // minimumInterval: 12 * 60 // 12h in minutes
        minimumInterval: 15
    })
    console.log("Registered background task");
});

export default function NotificationsProvider({ children }: Props) {
	useEffect(() => {
		if (resolver) {
            resolver()
            console.log("resolver called")
        }

	}, []);

	return (
		<NotificationsContext.Provider value={null}>
			{children}
		</NotificationsContext.Provider>
	);
}
