<!-- omit in toc -->
# Setup

Declarative Gradle [samples](./samples.md) require nightly versions of Gradle and Android Studio.
They all use the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) to point to the right Gradle version, so you don't have to worry about installing a specific version of Gradle.

To try out the samples and see all of the features, you need to install a few other components as described below:

- [JDK](#jdk)
- [Android Studio](#android-studio)
- [Install the Gradle Client](#install-the-gradle-client)
- [Pick a sample](#pick-a-sample)

## JDK

Make sure to use a JDK >= 17 and that your `JAVA_HOME` points to it.

You can use a JDK from any vendor.
We recommend [Eclipse Temurin™ (OpenJDK)](https://adoptium.net/temurin/releases/).

## Android Studio

Download and install the latest Android Studio Nightly from the [Studio Nightly](https://developer.android.com/studio/nightly) official page.

It can also be installed via the [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/).

Declarative features are only available on the latest nightly releases.

<!-- omit in toc -->
### Enable more declarative features in Studio

!!! info
    While syntax highlighting of `.gradle.dcl` files works out of the box in Studio nightlies, other features require flags to be enabled.

1. Open _Help -> Open Custom Vm Options_
2. Add text `-Didea.is.internal=true`
3. Restart the IDE
4. Open _Tools -> Internal Actions -> Android -> Edit Studio Flags_ and type _"Gradle Declarative"_ in the search window
5. Switch on the _Studio support for declarative files_ flags.

## Install the Gradle Client

The _Gradle Client_ is a standalone application used to demonstrate declarative features not yet implemented in the IDE.

Download the latest release from the [Gradle Client](https://github.com/gradle/gradle-client/releases/latest) repository on GitHub and install it.

The `DMG` file is for macOS, the `DEB` file is for Linux and the `MSI` file is for Windows.

## Pick a sample

After you've installed everything, pick out a [samples](./samples.md) to try the Declarative Gradle features.
