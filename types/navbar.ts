import type MaterialSymbols from "@/components/MaterialSymbols";

export interface NavbarOption {
	icon: keyof typeof MaterialSymbols.glyphMap;
	route: string;
	name: string;
}
