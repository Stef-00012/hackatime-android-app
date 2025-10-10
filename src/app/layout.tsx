import { red } from "@/constants/hcColors";
import type { Metadata, Viewport } from "next";
import "./globals.css";

export const metadata: Metadata = {
	title: "Hackatime Android App",
	description: "An android app made to view your Hackatime stats & projects.",
	openGraph: {
		title: "Hackatime Android App",
		description: "An android app made to view your Hackatime stats & projects.",
		type: "website",
	},
};

export const viewport: Viewport = {
	colorScheme: "dark",
	themeColor: red,
	width: "device-width",
	initialScale: 1,
};

export default function RootLayout({
	children,
}: Readonly<{
	children: React.ReactNode;
}>) {
	return (
		<html lang="en">
			<body className={`antialiased`}>{children}</body>
		</html>
	);
}
