
# Case Study - Converting Gradle-Client

## Introduction

In Feb/March 2025, using the version of Declarative Gradle that would be made available as EAP 3, we migrated the [`gradle client`](https://github.com/gradle/gradle-client) application to Declarative Gradle, including using a custom Software Type that described the tools and features used by one of its projects.
The Gradle Client is a desktop Kotlin Compose application that uses the Gradle tooling API to provide a rich GUI for analyzing Declarative Gradle build.

This migration allowed us to further explore this process and understand typical stumbling blocks and areas that need improvement.

This document outlines that experience, our findings, and provides a case study that should approximate a real-world migration.
You can see the results by comparing [the initial state of the repo](https://github.com/gradle/gradle-client/tree/4fb821caecbc4a55c5896624b31b201f66cc9fd0) with the state [after the migration was completed](https://github.com/gradle/gradle-client/commit/5484d947379fb7d8b4e2f00322bd3f37ba0f486f).

## Overview

At a high-level, we followed [the guidance](migration-guide.md) in our documentation with one exception.
If you browse the repository commits you'll notice one change we made was to migrate the _most_ complex project first.
This was done inorder to explore the process, and is not recommended for your own efforts.

## Initial Setup

1. Ensure the latest Gradle wrapper version

Features are constantly being added to the Declarative Gradle support.  
Any project interested in experimenting with DCL files should ensure they are using at least the latest stable Gradle release.
For the migration of the Gradle-Client project, we evetually upgraded the wrapper to the latest stable Gradle build [gradle-8.14-milestone-4](https\://services.gradle.org/distributions/gradle-8.14-milestone-4-bin.zip) in order to get support for the very latest Declarative Gradle features.
It is likely that by the time you are reading this, there will be a later milestone or release candidate of Gradle 8.14 available that would be usable for your own migrations.

3. Identify the project to be migrated first

We decided to start with the [`gradle-client` subproject](https://github.com/gradle/gradle-client/tree/main/gradle-client), as this was the most important and fully featured project contained in the Gradle Client build, which would be the most interesting to investigate.
For your own migrations, you should probably consider which projects would be _simplest_ to migrate.
Projects that are simpler and most similar to our declarative samples are good candidates.


4. Enable Software Types in Kotlin DSL Scripts
Set the org.gradle.kotlin.dsl.dcl flag in the root project's `gradle.properties` file.
This will allow Gradle to understand Software Types used in `*.kts` buildscripts.

3. Add the proper ecosystem plugin

The Declarative Gradle prototype maintains a KMP prototype plugin.
As the `gradle-client` project is a Kotlin Multiplatform project, we added this plugin to the project's existing `settings.gradle.kts` file.
Also, be sure to include the `gradlePluginPortal()` as a repository if your project doesn't already include this.

In `/settings.gradle.dcl`, add, if not already present:
```
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.experimental.kmp-ecosystem").version("0.1.41")
}

```

4. Use the existing prototype plugin's software types to migrate basics

Thing like declaring typical `implementation`, `api` and testing dependencies should be already supported out-of-the-box with the existing Software Types provided by Gradle's prototype plugins.
Migrating your dependencies to these is a good test of your setup.
Remember that Declarative Software Types can be used from KTS files.

We added the following to the `/gradle-client/build.gradle.kts` file:


```
kotlinApplication {
    targets {
        jvm {
            jdkVersion = 17

            dependencies {
                implementation(project(":build-action"))
                implementation(project(":mutations-demo"))

                implementation(project.dependencies.platform(libs.kotlin.bom))
                implementation(project.dependencies.platform(libs.kotlinx.coroutines.bom))
                implementation(project.dependencies.platform(libs.kotlinx.serialization.bom))
                implementation(project.dependencies.platform(libs.ktor.bom))

                implementation(libs.gradle.tooling.api)

                implementation(libs.sqldelight.extensions.coroutines)
                implementation(libs.sqldelight.runtime)

                // ...
            }
        }
    }
}
```

And we _removed_ these dependencies from the `jvmMain.dependencies` block where they were previously declared.
As this KMP project was only targeting the JVM platform, replacing the declarations in the `jvmMain.dependencies` with the `jvm.dependencies` block, will add them to the same configurations when the Software Type plugin applied, and the dependencies available to the project will remain identical after this change.

Now the prototype plugin should be take care of wiring these dependencies into the appropriate configurations, and the project should continue to build and run without change.

This represents the minimal configuration needed to make use of this `kotlinApplication` Software Type - which is why this snippet also declares the required JDK version information.

Continue to migrate each of your dependencies from the global `dependencies` block into the appropriate, more specific `dependencies` block provided by the new Software Type.
The goal is to completely delete the global block.

5. Setup an included build to define custom plugins

We can't just continue to use the `kotlinApplication` plugin type.
This type doesn't offer any support for SQL Delight, or Detekt, or other build configuration specific to this project.
This type can't yet be combined with other Declarative Gradle features in an extensible way (support for Composition of Software Types and Software Type Features is being actively developed).
We have to use an included build instead of `buildSrc` to contain the custom plugins.
This is because `buildSrc` is built after the project's settings script is evaluated, so it can't contain `Plugin<Settings>` that are meant to be used in this build, and we will need to write one of those.

... shows setup


6. Create the new Plugins

We can't use `extend` on the existing plugin class, because of a technicial limitation - each Software Type plugin can expose only a single software type.
So we'll have to create some 

... continues like this through basics of setting up the plugins

7. Write new extensions per each "major domain type" - Detekt, SQLDelight, etc.

Art more than science, etc.
Test build with them empty
Discuss dependency modifiers for `platform`

8. Migrate buildscript to use new types
Build should run, probably compile errors

9. Wire plugins
Discuss need for afterEvaluate...

10. Delete everything non-declarative
Convert files to DCL
Build should still run





## Remaining TODOs

Discuss stuff we couldn't migrate.
Discuss limitations (biggest is reliance on afterEvaluate if wiring plugins without lazy props)
Discuss imports, unqualified enums, etc.

## Conclusion

There's no easy button, but it's not THAT hard...
It’s really nice to add something to the extension interface, hit refresh, and then get type-safe assistance on it immediately in the project build...







TODO
- Mention that the platform didn't work in DCL until some recent changes
- Mention that in a mature D-G ecosystem, we can imagine finding existing Software Type Feature plugins for Detekt, Compose and SQLDelight support that could be mixed into an existing Kotlin ecosystem plugins. For now, however, composability is still a work in progress.





