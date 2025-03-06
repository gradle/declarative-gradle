# Example Migration

In Feb/March 2025, using the version of Declarative Gradle that would be made available as EAP 3, we migrated the [`gradle client`](https://github.com/gradle/gradle-client) to using DCL build files and a custom Software Type that described the tools and features used by this particular build.

This migration allowed us to further explore this process and understand typical stumbling blocks and areas that need improvement.

This document outlines that experience and our findings, and provides a case study that should approximate a real-world migration.
You can see the results by comparing [the initial state of the repo](https://github.com/gradle/gradle-client/tree/4fb821caecbc4a55c5896624b31b201f66cc9fd0) with the state [after the migration was completed](https://github.com/gradle/gradle-client/commit/0c324821e52e82a14413f0d195ad3dda8dc687df).

# How to Convert an Existing Project to Declarative Gradle

The first thing to keep in mind is that using Declarative Gradle is *not* an all or nothing proposition.
Gradle has always supported mixing build script DSLs between projects in a multi-project build using Groovy and Kotlin DSLs.
It continues to support doing the same with DCL buildscripts, which allows us to migrate project by project.

# Migration Process for Gradle-Client

## Initial Setup

1. Ensure the latest Gradle wrapper version

Features are constantly being added to the Declarative Gradle support.  
Any project interested in experimenting with DCL files should ensure they are using at least the latest stable Gradle release.
For the migration of the Gradle-Client project, we upgraded the wrapper to use a nightly Gradle build [gradle-8.14-20250222002553+0000](https\://services.gradle.org/distributions-snapshots/gradle-8.14-20250222002553+0000-bin.zip) in order to get support for the very latest Declarative Gradle features.
It is likely that by the time you are reading this, there will be a later milestone or release candidate of Gradle 8.14 available that would be usable for your own migrations.

3. Identify the project to be migrated first

We decided to start with the [`gradle-client` subproject](https://github.com/gradle/gradle-client/tree/main/gradle-client), as this was the most important and fully featured project contained in the Gradle Client build, which would be the most interesting to investigate.
For your own migrations, you should probably consider which projects would be _simplest_ to migrate.
Projects that are simpler and most similar to our declarative samples are good candidates.
If your project _doesn't_ already use Gradle's Kotlin DSL, you'll want to migrate to this first from your Groovy build using the existing [migration guide](https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html) in the Gradle documentation.

3. Add the proper ecosystem plugin

The Declarative Gradle prototype maintains a KMP prototype plugin.
As the `gradle-client` project is a Kotlin Multiplatform project, we added this plugin to the project's existing `settings.gradle.kts` file.
Also, be sure to include the `gradlePluginPortal()` as a repository if your project doesn't already include this.


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

We added the following to the `gradle-client` project's `build.gradle.kts` file:


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
Set the org.gradle.kotlin.dsl.dcl flag in the project's properties file.
Now the prototype plugin should be take care of wiring these dependencies into the appropriate configurations, and the project should continue to build and run without change.

This represents the minimal configuration needed to make use of this `kotlinApplication` Software Type - which is why this snippet also declares the required JDK version information.

Continue to do this for each of your dependencies.

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


# Hints and Tips

- Add `@file:Suppress("UnstableApiUsage")` to the top of your KTS files during the intermediate stages to silence warnings about many of the DCL types.
- DCL does not yet support Version Catalogs, so if you're using one, you'll need to convert your `libs.my.lib` dependency declarations to GAV coordinates like `org:mylib:1.4` when you're ready to switch from using Kotlin DSL files with Software Types to fully declarative DCL files.  This is annoying, but temporary.
- DCL files don't allow infix notation.  If you were applying plugins like:

```
    plugins {
        id("my.plugin") version "0.4"
    }
```

switch to using chained calls like:
```
    plugins {
        id("my.plugin").version("0.4")
    }
```


## Footguns
- It’s easy to forget to set org.gradle.kotlin.dsl.dcl flag in properties, but this is necessary to use Software Type plugins in non-DCL (KTS) files.
- NDOC discussion...
- Using the wrong annotation types
- Other stuff from my notes
- Unnecessary brackets are actually necessary...


