import type MaterialSymbols from "@/components/MaterialSymbols";

interface SidebarOptionButton {
	icon: keyof typeof MaterialSymbols.glyphMap;
	type: "button";
	route: string;
	name: string;
	subMenus: [];
}

interface SidebarOptionSelect {
	icon: keyof typeof MaterialSymbols.glyphMap;
	subMenus: SidebarOption[];
	type: "select";
	name: string;
	route: null;
}

export type SidebarOption = SidebarOptionButton | SidebarOptionSelect;
