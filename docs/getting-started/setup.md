<!-- omit in toc -->
# Setup

Declarative Gradle [samples](./samples.md) require nightly versions of Gradle and IDE/Plugins, see below.
They all use the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) to point to the right Gradle version, so you don't have to worry about installing a specific version of Gradle.

To try out the samples and see all of the features, you need to install a few other components as described below:

- [JDK](#jdk)
- [IDE](#ide)
  - [Android Studio](#android-studio)
  - [IntelliJ IDEA](#intellij-idea)
  - [Visual Studio Code](#visual-studio-code)
  - [Eclipse IDE](#eclipse-ide)
- [Gradle Client](#gradle-client)
- [Pick a sample](#pick-a-sample)

## JDK

Make sure to use a JDK >= 17 and that your `JAVA_HOME` points to it.

You can use a JDK from any vendor.
We recommend [Eclipse Temurin™ (OpenJDK)](https://adoptium.net/temurin/releases/).

## IDE

### Android Studio

Download and install an [Android Studio Nightly](https://developer.android.com/studio/nightly) release.

<!-- omit in toc -->
#### Enable declarative features in Studio

While syntax highlighting of `.gradle.dcl` files works out of the box in Studio nightlies, other features require flags to be enabled.

1. Enable the [IDE internal mode](https://plugins.jetbrains.com/docs/intellij/enabling-internal.html) by selecting _Help_ -> _Edit Custom Properties_. This selection opens the `idea.properties` file. If it does not exist, the IDE will prompt to create one. Add a line with `idea.is.internal=true`, save the file and restart the IDE.
2. Open _Tools_ -> _Internal Actions_ -> _Registry_
3. Search for the Declarative Gradle flags by typing `declarative`
4. Enable the `gradle.declarative.studio.support` and `gradle.declarative.ide.support` flags
5. Restart the IDE

### IntelliJ IDEA

Download and install the latest [IntelliJ 2025.2 EAP](https://www.jetbrains.com/idea/nextversion/) release. Earlier releases do not fully support DCL features. 

Follow the same instructions as with [Android Studio](#android-studio) above to enable Declarative features.

### Visual Studio Code

Download the [Declarative Gradle VSIX](https://gradle.github.io/declarative-vscode-extension/) and install it in your Visual Studio Code.

This extension was also tested in GitHub Codespace and should work in any Visual Studio Code derivative.

### Eclipse IDE

Install the Declarative Gradle editor support for the Eclipse IDE from Buildship snapshot [Update Site](https://github.com/eclipse/buildship/blob/master/docs/user/DeclarativeGradle.md) in our Eclipse IDE.

Make sure to follow the [setup instructions](https://github.com/eclipse/buildship/blob/master/docs/user/DeclarativeGradle.md#setup) as more steps are needed for this to work.

## Gradle Client

The _Gradle Client_ is a standalone application used to demonstrate declarative features not yet implemented in the IDE.

Download the latest release from the [Gradle Client](https://github.com/gradle/gradle-client/releases/latest) repository on GitHub and install it.

The `DMG` file is for macOS, the `DEB` file is for Linux and the `MSI` file is for Windows.

!!! warn
    Note that the _Gradle Client_ is not signed/notarized and will require you to accept running it anyway.

## Pick a sample

After you've installed everything, pick out a [samples](./samples.md) to try the Declarative Gradle features.
