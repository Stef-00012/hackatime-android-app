import type { SidebarOption } from "@/types/sidebar";

export const sidebarOptions: SidebarOption[] = [
    {
        type: "button",
        route: "/",
        name: "Home",
        icon: "home",
        subMenus: [],
    },
    {
        type: "button",
        route: "/projects",
        name: "Projects",
        icon: "dashboard",
        subMenus: [],
    },

    // {
    //     type: "select",
    //     route: null,
    //     name: "test",
    //     icon: "folder",
    //     subMenus: [
    //         {
    //             type: "button",
    //             route: "/",
    //             name: "Home",
    //             icon: "home",
    //             subMenus: [],
    //         },
    //     ]
    // }
];
