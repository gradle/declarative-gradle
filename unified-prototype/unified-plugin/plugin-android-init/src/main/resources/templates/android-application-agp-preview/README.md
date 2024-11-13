# declarative-samples-android-app
A sample Android application written in the Declarative Gradle DSL, using the official Android Software Types Preview `androidApplication` and `androidLibrary` defined in the `com.android.ecosystem` ecosystem plugin.

## Building and Running

This sample shows the definition of a multiproject Android application implemented using Kotlin source code.

To build the project without running, use:

```shell
./gradlew build
```

To run the application, first install it on a connected Android device using:

```shell
./gradlew :app:installDebug
```

In IntelliJ IDEA or Android Studio you can use the `app` run configuration to launch the app in an emulator to see a hello world message.
