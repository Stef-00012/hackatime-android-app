import Button from "@/components/Button";
import { muted, red } from "@/constants/hcColors";
import { styles } from "@/styles/components/textInput";
import { type ReactNode, useState } from "react";
import {
	type ColorValue,
	type KeyboardType,
	TextInput as NativeTextInput,
	type ReturnKeyTypeOptions,
	type StyleProp,
	type TextInputChangeEvent,
	type TextInputSubmitEditingEvent,
	type TextStyle,
	View,
	type ViewStyle,
} from "react-native";
import type MaterialSymbols from "./MaterialSymbols";
import Text from "./Text";

interface Props {
	value?: string;
	defaultValue?: string;
	title?: string;
	description?: string | ReactNode;
	id?: string;
	onPasswordToggle?: (
		visibile: boolean,
		id?: Props["id"],
	) => void | Promise<void>;
	onValueChange?: (newValue: string, id?: Props["id"]) => void | Promise<void>;
	disabled?: boolean;
	disableContext?: boolean;
	showDisabledStyle?: boolean;
	multiline?: boolean;
	password?: boolean;
	keyboardType?: KeyboardType;
	placeholder?: string;
	sideButtonType?: "primary" | "outline" | "transparent" | "secondary";
	sideButtonIconColor?: ColorValue;
	sideButtonTextStyle?: StyleProp<TextStyle>;
	sideButtonButtonStyle?: StyleProp<ViewStyle>;
	sideButtonContainerStyle?: StyleProp<ViewStyle>;
	onSideButtonPress?: (id?: Props["id"]) => void | Promise<void>;
	sideButtonIcon?: keyof typeof MaterialSymbols.glyphMap;
	maxLength?: number;
	inputStyle?: TextStyle;
	showSoftInputOnFocus?: boolean;
	onChange?: (
		event: TextInputChangeEvent,
		id?: Props["id"],
	) => void | Promise<void>;
	onSubmitEditing?: (
		event: TextInputSubmitEditingEvent,
		id?: Props["id"],
	) => void | Promise<void>;
	returnKeyType?: ReturnKeyTypeOptions;
	onBeforeSideButtonPress?: (
		id?: Props["id"],
		isPasswordVisible?: boolean,
	) => boolean | Promise<boolean>;
}

export default function TextInput({
	value,
	onValueChange = () => {},
	onPasswordToggle = () => {},
	onSideButtonPress = () => {},
	onBeforeSideButtonPress = () => true,
	disabled = false,
	showDisabledStyle = true,
	disableContext = false,
	multiline = false,
	title,
	description,
	password = false,
	keyboardType = "default",
	placeholder,
	sideButtonType = "primary",
	sideButtonIcon,
	sideButtonIconColor = "white",
	sideButtonTextStyle,
	sideButtonButtonStyle,
	sideButtonContainerStyle,
	maxLength,
	inputStyle,
	showSoftInputOnFocus = true,
	onChange = () => {},
	onSubmitEditing = () => {},
	returnKeyType,
	id,
	defaultValue,
}: Props) {
	const [displayPassword, setDisplayPassword] = useState<boolean>(false);

	if (password)
		return (
			<View>
				{title && (
					<>
						<Text
							style={{
								...styles.inputHeader,
								...(!description && {
									marginBottom: 5,
								}),
								...(disabled &&
									showDisabledStyle &&
									styles.inputHeaderDisabled),
							}}
						>
							{title}
						</Text>

						{description && (
							<Text style={styles.inputDescription}>{description}</Text>
						)}
					</>
				)}
				<View
					style={{
						...styles.inputContainer,
					}}
				>
					<NativeTextInput
						selectionColor={red}
						showSoftInputOnFocus={showSoftInputOnFocus}
						multiline={multiline}
						secureTextEntry={!displayPassword}
						onChange={(event) => onChange(event, id)}
						onSubmitEditing={(event) => onSubmitEditing(event, id)}
						maxLength={maxLength}
						style={{
							...styles.textInput,
							...styles.textInputSideButton,
							...(disabled && showDisabledStyle && styles.textInputDisabled),
							...inputStyle,
						}}
						editable={!disabled}
						contextMenuHidden={disableContext}
						onChangeText={(text) => onValueChange(text, id)}
						value={value}
						placeholder={placeholder}
						placeholderTextColor={muted}
						returnKeyType={returnKeyType}
						defaultValue={defaultValue}
					/>
					<Button
						onPress={async () => {
							const shouldRun = await onBeforeSideButtonPress(
								id,
								displayPassword,
							);

							if (!shouldRun) return;

							onPasswordToggle(!displayPassword, id);
							onSideButtonPress(id);
							setDisplayPassword((prev) => !prev);
						}}
						icon={displayPassword ? "visibility_off" : "visibility"}
						type={sideButtonType}
						containerStyle={[
							sideButtonContainerStyle,
							{
								marginLeft: 10,
							},
						]}
						textStyle={sideButtonTextStyle}
						buttonStyle={sideButtonButtonStyle}
						iconColor={sideButtonIconColor}
					/>
				</View>
			</View>
		);

	if (onSideButtonPress && sideButtonIcon)
		return (
			<View>
				{title && (
					<>
						<Text
							style={{
								...styles.inputHeader,
								...(!description && {
									marginBottom: 5,
								}),
								...(disabled &&
									showDisabledStyle &&
									styles.inputHeaderDisabled),
							}}
						>
							{title}
						</Text>

						{description && (
							<Text style={styles.inputDescription}>{description}</Text>
						)}
					</>
				)}
				<View
					style={{
						...styles.inputContainer,
					}}
				>
					<NativeTextInput
						selectionColor={red}
						onChange={(event) => onChange(event, id)}
						onSubmitEditing={(event) => onSubmitEditing(event, id)}
						showSoftInputOnFocus={showSoftInputOnFocus}
						multiline={multiline}
						maxLength={maxLength}
						style={{
							...styles.textInput,
							...styles.textInputSideButton,
							...(disabled && showDisabledStyle && styles.textInputDisabled),
							...inputStyle,
						}}
						editable={!disabled}
						contextMenuHidden={disableContext}
						onChangeText={(text) => onValueChange(text, id)}
						value={value}
						keyboardType={keyboardType}
						placeholder={placeholder}
						placeholderTextColor={muted}
						returnKeyType={returnKeyType}
						defaultValue={defaultValue}
					/>
					<Button
						onPress={async () => {
							const shouldRun = await onBeforeSideButtonPress(id);

							if (!shouldRun) return;

							onSideButtonPress(id);
						}}
						icon={sideButtonIcon}
						type={sideButtonType}
						containerStyle={[
							sideButtonContainerStyle,
							{
								marginLeft: 10,
							},
						]}
						textStyle={sideButtonTextStyle}
						buttonStyle={sideButtonButtonStyle}
						iconColor={sideButtonIconColor}
					/>
				</View>
			</View>
		);

	return (
		<View>
			{title && (
				<>
					<Text
						style={{
							...styles.inputHeader,
							...(!description && {
								marginBottom: 5,
							}),
							...(disabled && showDisabledStyle && styles.inputHeaderDisabled),
						}}
					>
						{title}
					</Text>

					{description && (
						<Text style={styles.inputDescription}>{description}</Text>
					)}
				</>
			)}
			<NativeTextInput
				selectionColor={red}
				onChange={(event) => onChange(event, id)}
				onSubmitEditing={(event) => onSubmitEditing(event, id)}
				showSoftInputOnFocus={showSoftInputOnFocus}
				multiline={multiline}
				maxLength={maxLength}
				style={{
					...styles.textInput,
					...(disabled && showDisabledStyle && styles.textInputDisabled),
					...inputStyle,
				}}
				editable={!disabled}
				contextMenuHidden={disableContext}
				onChangeText={(text) => onValueChange(text, id)}
				value={value}
				keyboardType={keyboardType}
				placeholder={placeholder}
				placeholderTextColor={muted}
				returnKeyType={returnKeyType}
				defaultValue={defaultValue}
			/>
		</View>
	);
}
