<!-- omit in toc -->
# Setup

Declarative Gradle [samples](./samples.md) require nightly versions of Gradle and Android Studio.
They all use the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) to point to the right Gradle version so you don't have to worry about that.

To try out the samples and be able to reproduce all features you need to install a few components as described below:

- [JDK](#jdk)
- [Android Studio](#android-studio)
- [Gradle Client](#gradle-client)

## JDK

Make sure to use a JDK >= 17 and that your `JAVA_HOME` points to it.

You can use a JDK from any vendor.
We recommend [Eclipse Temurin™ (OpenJDK)](https://adoptium.net/temurin/releases/).

## Android Studio

First, download and install the latest Android Studio Nightly.

It can be found on the [Studio Nightly](https://developer.android.com/studio/nightly) official page.
It can also be installed via the [JetBrains ToolBox](https://www.jetbrains.com/toolbox-app/) application.

<!-- omit in toc -->
### Enable more declarative features in Studio

!!! info
    While syntax highlighting of `.gradle.dcl` files works out of the box in Studio nightlies, other features require to enable for flags.

1. Open _Help -> Open Custom Vm Options_
2. Add text `-Didea.is.internal=true`
3. Restart the IDE
4. Open _Tools -> Internal Actions -> Android -> Edit Studio Flags_ and type _"Gradle Declarative"_ in the search window
5. Switch on the _Studio support for declarative files_ flags.

## Gradle Client

The _Gradle Client_ is a standalone application used to demonstrate declarative features not yet implemented in the IDE.

Download the latest release from the [Gradle Client](https://github.com/gradle/gradle-client/releases) repository on GitHub and install it.
A `DMG` file for Mac, a `DEB` file for Linux and a `MSI` file for Windows are attached to each release.
