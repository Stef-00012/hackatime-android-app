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
        <td><img width="1080" height="2400" alt="image1" src="./assets/app/image1.png" /></td>
        <td><img width="1080" height="2400" alt="image2" src="./assets/app/image2.png" /></td>
        <td><img width="1080" height="2400" alt="image3" src="./assets/app/image3.png" /></td>
    </tr>
    <tr>
        <td><img width="1080" height="2400" alt="image4" src="./assets/app/image4.png" /></td>
        <td><img width="1080" height="2400" alt="image5" src="./assets/app/image5.png" /></td>
        <td><img width="1080" height="2400" alt="image6" src="./assets/app/image6.png" /></td>
    </tr>
    <tr>
        <td><img width="1080" height="2400" alt="image7" src="./assets/app/image7.png" /></td>
        <td><img width="1080" height="2400" alt="image8" src="./assets/app/image8.png" /></td>
        <td><img width="1080" height="2400" alt="image9" src="./assets/app/image9.png" /></td>
    </tr>
    <tr>
        <td><img width="1080" height="2400" alt="image7" src="./assets/app/image10.png" /></td>
    </tr>
</table>

</details>

# Download

The app is available on the following platforms:
- Google Play Store
- GitHub Releases

[![Get on Google Play](/assets/google-play.png)](https://play.google.com/store/apps/details?id=com.stefdp.hackatime)
[![Get on GitHub](/assets/github.png)](https://git.stefdp.com/Stef/hackatime-android-app/releases/latest)

# Creating a development build

> [!NOTE]
> The build will fail unless you have a `google-services.json` file obtained from firebase in the `app/` folder

To create a development build just run `./gradlew assembleDebug` or use the Android Studio Emulator

This will create an APK in `app/build/outputs/apk/debug/app-debug.apk`

> Building an APK

just run `./gradlew assembleRelease`

This will create an APK in `app/build/outputs/apk/release/app-release(-unsigned).apk`