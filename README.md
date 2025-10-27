# Hackatime Android App

> [!IMPORTANT]
> iOS is not supported

This is an android app made to view your [Hackatime](https://hackatime.hackclub.com) data.

# Features

- View your time, top language and top project for any range
- View your top editor, top OS and top machines when the range is set to "Last 7 Days"
- View your projects
- Lock the app behind biometric authentication
- Widgets
- Motivational Push Notifications

# Images

<details>

<summary>Click here to view the app's images</summary>

<table>
    <tr>
        <td><img width="1080" height="2340" alt="image1" src="https://github.com/user-attachments/assets/bc7aff48-fb50-4e4f-b555-0f72ffc1abf3" /></td>
        <td><img width="1080" height="2340" alt="image2" src="https://github.com/user-attachments/assets/90bbbd0f-fd7f-40bb-a6f2-559f52bce239" /></td>
        <td><img width="1080" height="2340" alt="image3" src="https://github.com/user-attachments/assets/a0eb8f37-b3c5-44b5-b588-aecd2122ecf7" /></td>
    </tr>
    <tr>
        <td><img width="1080" height="2340" alt="image4" src="https://github.com/user-attachments/assets/af1f963c-52cd-48f9-90c1-ccfe8b066f24" /></td>
        <td><img width="1080" height="2340" alt="image5" src="https://github.com/user-attachments/assets/49d1664e-5b70-488f-a377-c0545c316e7c" /></td>
        <td><img width="1080" height="2340" alt="image6" src="https://github.com/user-attachments/assets/a417ea7d-20a2-4e08-9c59-bf4ba315fdfc" /></td>
    </tr>
    <tr>
        <td><img width="1080" height="2340" alt="image7" src="https://github.com/user-attachments/assets/e50ffa1f-8542-4ffc-9285-e589f768884e" /></td>
        <td><img width="1080" height="2340" alt="image8" src="https://github.com/user-attachments/assets/7623ba98-1f7f-4b0b-8596-59b6f5b39326" /></td>
        <td><img width="1080" height="2340" alt="image9" src="https://github.com/user-attachments/assets/76b83728-6c7d-4e88-a6f7-fafc0e7220e9" /></td>
    </tr>
</table>

</details>

# Download

The app is available on the following platforms:
<!-- - Google Play Store -->
- GitHub Releases

<!-- [![Get on Google Play](/assets/github/google-play.png)](https://play.google.com/store/apps/details?id=com.stefdp.hackatime) -->
[![Get on GitHub](/assets/github/github.png)](https://github.com/Stef-00012/zipline-android-app/releases/latest/download/app-release.apk)

# Creating a development build

> [!IMPORTANT]
> Requires an android device connected to the laptop/PC and ADB (Android Debug Bridge) installed

1. `bun install`
2. `bun run materialSymbols:setup`
3. `bun run prebuild`
4. `bun run run:android`

(this will create a development APK and automatically install in your device)

> Building an APK

> [!NOTE]
> add `--local` flag if you don't have a Expo EAS account or want to build it locally on your PC\
> add `--output /path/to/file.apk` to save the APK in that path

1. `bun install`
2. `bun run materialSymbols:setup`
3. `bun install --global eas-cli`
4. `eas build --clear-cache --platform android --profile preview`

This will create an APK but won't automatically install

# Some info

The latest update of this app is also shipped to [moonshot](https://moonshot.hackclub.com)

<div align="center">
  <a href="https://moonshot.hackclub.com" target="_blank">
    <img src="https://hc-cdn.hel1.your-objectstorage.com/s/v3/35ad2be8c916670f3e1ac63c1df04d76a4b337d1_moonshot.png" 
         alt="This project is part of Moonshot, a 4-day hackathon in Florida visiting Kennedy Space Center and Universal Studios!" 
         style="width: 100%;">
  </a>
</div>
