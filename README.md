# Genesis Android

Genesis is an offline-first, text-led life simulator based on the existing Artist Story game. It is a native Android app built with Kotlin, Jetpack Compose, and Room (SQLite). NPCs, music platforms, social feeds, the in-game web, phone, and business systems simulate locally; they do not require an AI service or a real social-media account.

## Open and run

1. Open this folder in Android Studio.
2. Let Android Studio install or select a compatible JDK and Android SDK, then allow Gradle sync to finish.
3. Start an Android emulator or connect a phone with developer mode enabled.
4. Select the `app` run configuration and press **Run**.

The debug build uses Android's standard debug signing key. No Google account, backend, API key, tunnel, or internet connection is needed to play. Internet access is currently used only for optional remote artwork; the game simulation and saves are local.

## Test on a physical Android phone (Windows)

The simplest route is USB; Expo, a tunnel, and an emulator are not involved.

1. Install Android Studio on the computer and open this `Genesis-Android` folder. Allow the first Gradle sync to finish while connected to the internet; this downloads the build tools and libraries.
2. On the phone, open **Settings → About phone** and tap **Build number** seven times. In **Developer options**, enable **USB debugging**.
3. Connect the phone to the computer with a data-capable USB cable, unlock it, and accept the **Allow USB debugging** prompt.
4. In Android Studio, select the phone in the device menu beside **Run**, select the `app` configuration, then press **Run**. Android Studio builds and installs the debug version directly onto the phone.
5. Open Genesis on the phone. After installation, the simulation and saves work offline. Keep internet available when rebuilding so Gradle can retrieve any dependencies that are not cached yet.

To create an APK you can copy to the phone, choose **Build → Build Bundle(s) / APK(s) → Build APK(s)**. Android Studio shows a link when it finishes; the file is `app/build/outputs/apk/debug/app-debug.apk`. Copy it to the phone and open it from Files to install. If Android asks, allow Files to install unknown apps for this one installation source. This debug APK is for testing; Play Store release signing is a separate step.

## Build and verify

Use the Gradle tasks `:app:assembleDebug` and `:app:testDebugUnitTest` from Android Studio's Gradle panel or terminal. Install `app/build/outputs/apk/debug/app-debug.apk` on a device to play the same debug build used during development.

Release signing is intentionally external to source control. Set `GENESIS_KEYSTORE_PATH`, `GENESIS_STORE_PASSWORD`, `GENESIS_KEY_ALIAS`, and `GENESIS_KEY_PASSWORD` in the release environment before producing a Play Store artifact. Never place the upload key in the project folder or share it in an archive.

## Current product scope

The existing project already has artist creation, weekly progression, music releases and streaming, NPC relationships, Cast-style social posts, SeeChat-style conversations, an in-game web hub, a phone, businesses, and local save/load. This is an early playable foundation, not a finished life simulator: player life stages and varied non-artist starting paths still need deliberate design, and the remaining in-game screens need an interaction-by-interaction completion audit. The priority is to make connected systems respond to the same world and leave a readable history of outcomes before expanding the feature list.

Recent simulator repairs connect in-game messaging to NPC and partner history, charge one energy for the first new contact with each person in a game week, limit repeated positive relationship gains during that week, and make partner contact affect neglect decay. Weekly decay, breakup reports, and saved relationship status now use the same rule. Partner financial requests depend on trust and wealth context rather than gender. Inbox tabs filter the simulated messages by type.

The current application ID is retained from the supplied project. Verify Play Console ownership before changing it or publishing an update.
