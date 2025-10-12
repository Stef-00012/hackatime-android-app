import Text from "@/components/Text";
import { getUser } from "@/functions/server";
import { useEffect, useState } from "react";
import { View } from "react-native";

export default function Goals() {
	const [isApiKeyOnServer, setIsApiKeyOnServer] = useState<boolean>(false);

	useEffect(() => {
		getUser().then(setIsApiKeyOnServer);
	}, []);

	return (
		<View style={{ flex: 1 }}>
			{!isApiKeyOnServer && (
				<View style={{ zIndex: 9 }}>

                </View>
			)}

            
		</View>
	);
}
