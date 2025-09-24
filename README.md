# Hackatime Android App

> [!IMPORTANT]
> iOS is not supported

This is an android app made to view your [Hackatime](https://hackatime.hackclub.com) data.

# Features

- View your time, top language and top project for any range
- View your top editor, top OS and top machines when the range is set to "Last 7 Days"
- View your projects
- Lock the app behind biometric authentication

# Download

The app is available on the following platforms:
<!-- - Google Play Store -->
- GitHub Releases

<!-- [![Get on Google Play](/assets/github/google-play.png)](https://play.google.com/store/apps/details?id=com.stefdp.zipline) -->
[![Get on GitHub](/assets/github/github.png)](https://github.com/Stef-00012/zipline-android-app/releases/latest/download/app-release.apk)

# Creating a development build

> [!IMPORTANT]
> Requires an android device connected to the laptop/PC and ADB (Android Debug Bridge) installed

1. `bun install`
2. `bun run prebuild`
3. `bun run run:android`

(this will create a development APK and automatically install in your device)

> Building an APK

> [!NOTE]
> add `--local` flag if you don't have a Expo EAS account or want to build it locally on your PC\
> add `--output /path/to/file.apk` to save the APK in that path

1. `bun install`
2. `bun install --global eas-cli`
3. `eas build --clear-cache --platform android --profile preview`

This will create an APK but won't automatically install

# Known Issues

- If you enable biometric authentication, then upon opening the app you press the back button (refusing the biometric authentication), the app will correctly close, but if you try to reopen the app right after, it'll show a constant loading and no biometric authentication until the app is completely closed and reopened

# TODO
- [ ] fix constantly showing skeleton when there's no data on an account
- [ ] Add Skeleton UI for the projects loading
