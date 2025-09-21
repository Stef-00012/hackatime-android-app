import {
	createContext,
	type ReactNode,
	useCallback,
	useMemo,
	useState,
} from "react";

interface Props {
	children: ReactNode;
}

interface SidebarData {
	open: boolean;
	toggleOpen: (open?: boolean) => void;
}

export const SidebarContext = createContext<SidebarData>({
	open: false,
	toggleOpen: () => {},
});

export default function SidebarProvider({ children }: Props) {
	const [_open, _setOpen] = useState<boolean>(false);

	const toggleOpen = useCallback((open?: boolean) => {
		if (typeof open === "boolean") return _setOpen(open);

		_setOpen((prev) => !prev);
	}, []);

	const sidebarData = useMemo<SidebarData>(
		() => ({
			open: _open,
			toggleOpen,
		}),
		[_open, toggleOpen],
	);

	return (
		<SidebarContext.Provider value={sidebarData}>
			{children}
		</SidebarContext.Provider>
	);
}
