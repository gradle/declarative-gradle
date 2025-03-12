# Migrating Existing Builds to Declarative Gradle

## Introduction

This page provides overview of how to migrate existing builds to begin using Gradle's new Declarative Configuration Language (DCL) in an existing project.
It discusses pain points, gotchas and other interesting nuggets informed by an actual migration.

Note that as of this writing (early March 2025) DCL's support for composability and extensibility is not yet available in EAP 3, so the approach outlined here is not meant to be the final state of this process.
We recognize that there are major areas for improvement, and are actively exploring changes we hope to make available in the next EAP release.
However, despite these limitations, we think that this guide will still be useful to early adopters who are eager to experiment with DCL _now_.

## Migration Process

The important steps of the migration process to follow are outlined below.

1. Identify a project migration order

In is likely in a multi-project build that some projects will be much easier to convert to DCL than others.
The less imperative logic, third-party plugins, and atypical configuration is present in a buildscript, the easier it will be to convert.
Project that look similar to one of our [Declarative Samples](https://declarative.gradle.org/docs/getting-started/samples/), or the output of Gradle's built-in [build init plugin](https://docs.gradle.org/current/userguide/build_init_plugin.html), are good candidates to be converted first.
Starting with simpler projects allows you to verify that Declarative Gradle is properly setup and should make debugging issues with more complex projects easier.

Keep in mind is that using Declarative Gradle is *not* an all or nothing proposition.
Gradle has always supported mixing build script DSLs between projects in a multi-project build using Groovy and Kotlin DSLs.
It continues to support doing the same with DCL buildscripts, which allows us to migrate project by project.

2. Ready the project for DCL

First, the project you are converting _doesn't_ already use Gradle's Kotlin DSL, you should migrate to it using the existing [migration from Groovy DSL guide](https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html) in the Gradle documentation.
You should also ensure you are using the latest stable Gradle build, at least [the latest used by the Declarative Gradle prototype plugins](https://github.com/gradle/declarative-gradle/blob/main/unified-prototype/gradle/wrapper/gradle-wrapper.properties). 

Next, enable Software Types in Kotlin DSL Scripts by setting the `org.gradle.kotlin.dsl.dcl=true` flag in the root project's `gradle.properties` file.
This will allow Gradle to understand Software Types used in `*.kts` buildscripts alongside other imperative project configuration logic.

You'll also need to identify which declarative _ecosystem plugins_ (such as the [`jvm-ecosystem` plugin](https://plugins.gradle.org/plugin/org.gradle.experimental.jvm-ecosystem)) to apply in order to make the Software Types you wish to use available to your project.
See the page on [Software Features](https://declarative.gradle.org/docs/reference/software-features/) in the Declarative Gradle documentation for more information on these topics.

3. Declarativize your settings

Convert your `/settings.gradle.kts` file to a `/settings.gradle.dcl` file.
This requires renaming the file, and may involve commenting out or relocating to project buildscripts any features that are not currently supported by the DCL (for instance repository content filtering).

Apply the ecosystem plugins your build will use in the `plugins` block in this file (add one if it doesn't already exist) and verify your build still runs. 

4. Declarativize simple projects

Any Declarative Gradle builds (those that make use of a `settings.gradle.dcl` file) can include a mix of declarative projects using `*.dcl` buildscripts, and non-declarative projects using Gradle's Groovy or Kotlin DSLs.
So there is no need to convert every project in your build at once.
You should start with the simplest projects that can already be nicely described by existing declarative Software Types, such as Gradle's prototype ecosystem plugins.

Convert these projects one at a time, by renaming the file from `build.gradle.kts` to `build.gradle.dcl`, and replacing the entire contents of their buildscripts with the Software Type from the ecosystem plugin you applied to your settings file that describes it.

Configure the Software Type by adding dependencies, setting version information, project description, and any other property values to reproduce what was done in the original imperative buildscript.
This may involve commenting out or relocating any features that are not currently supported by the DCL or that Software Type.

Verify your build still runs. 

5. Setup a Composite build

The current lack of composability in DCL makes it very likely you'll need to create custom Software Types for some of the more complex projects in your builds.
After finishing converting the easy projects, you'll want to setup a [Composite build](https://docs.gradle.org/current/userguide/composite_builds.html#defining_composite_builds) that your build uses.

The additional build included by your composite builds (often called the "included build" due to the method used to register it in your settings file), is a fully independent builds that makes its products implicitly available to your build.
You can use a composite build to write declarative plugins that define new Software Types.
These Software Types can be published and used anywhere, but you will probably only want to use them within the build you are migrating.
Setting up a composite build will make this usage straightforward.

See the Gradle documentation linked above for information on how to do this. 

6. Create custom Software Types for complex projects

Now that you have a location to put it, it's time to define a new project in your included build that will hold your new plugins.
This project can _also_ use Gradle's DCL.
Create a `settings.gradle.dcl` file in the root of your included build and apply the Gradle Plugin Ecosystem plugin it it:

```
plugins {
    id("org.gradle.experimental.plugin-ecosystem").version("0.1.41")
}
```

This plugin will allow you declaratively create new Software Types in Java.
See the page on [Software Features](https://declarative.gradle.org/docs/reference/software-features/) in the Declarative Gradle documentation for more information on how to do this topics.
It's also helpful to look at some of [the prototype plugins](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/unified-plugin) built in our prototype.

You'll need to provide at least 4 parts:
- A top-level extension that will be configured in the DCL file by any project using this Software Type.
- A Software Type plugin, that implements `Plugin<Project>` and has a `public abstract` getter annotated with `@SoftwareType` that exposes that extension type.
- A "Ecosystem" plugin, that implements `Plugin<Settings>`, and registers your software type via `@RegistersSoftwareTypes(MySoftwareTypePlugin.class)`.
- Registration of the new plugins in the `build.gradle.dcl` file in the project that contains them, using the `registers` block provided by the `javaGradlePlugin` Software Type's top-level extension. 

Exactly how to build these Software Types depends on the particulars of the projects you intend to use them, what plugins those project use, and how those projects are configured in their existing buildscripts.  
Continue reading [a mini-"Case Study"](migration-case-study.md) of our efforts to convert an actual existing project.

8. Declarativize complex projects

Add your new Ecosystem plugin to the list of plugins your project's `settings.gradle.dcl` file.
Add your new Software Type extension to your project's existing `build.gradle.kts` file.
Now configure the new Software Type, moving things like verion and dependency declarations from the imperative parts of your buildscript, which comprises everything outside of your Software Type extension, to the declarative part inside of it.

This is likely iterative process, where you will incrementally migrate bits of configuration from the imperative part of your buildscript to the Software Type, then realize you are lacking functionality in the Software Type and reengineer it to expand its capabilitys, then continue migrating functionality, until you are finished.
Fortunately, the included build will make rapidly testing changes to your plugin and updating your buildscript very easy.
Verifying your build after moving each bit of configuration is highly recommended.

The goal is to completely migrate _everything_ contained in your buildscript to the Software Type so that the imperative part shrinks to empty.
After this is finished, you can rename the file to `build.gradle.dcl` and 

9. Continue until all projects are using DCL

Once you have your included build set up, and your first custom Software Type plugin used by one of your projects, you should find it easy to repeat this proces for all the projects in your build that require customization.

As all projects in your build are now fully declarative, you can delete the `org.gradle.kotlin.dsl.dcl=true` flag from your `gradle.properties`.

10. (Optional) Extract common project configuration to defaults

Now that you have all your projects converted, you can look to make your build more DRY by extracting common configuration to the `defaults` block in your root `settings.gradle.dcl` file.
For every Software Type you use, if you always set a certain property to the same value (for example, setting a jdk version), you can set this just once and avoid duplication.

See [the androidLibrary defaults](https://github.com/gradle/declarative-gradle/blob/main/unified-prototype/settings.gradle.kts#L27) in our Gradle prototype plugins repository for an example of this.

## Hints and Tips

- Add `@file:Suppress("UnstableApiUsage")` to the top of your KTS files during the intermediate stages to silence warnings about many of the DCL types.
- DCL does not yet support Version Catalogs, so if you're using one, you'll need to convert your `libs.my.lib` dependency declarations to GAV coordinates like `org:mylib:1.4` when you're ready to switch from using Kotlin DSL files with Software Types to fully declarative DCL files.  This is annoying, but temporary until a better solution to reusable version declarations is made available in a future EAP.

## Footguns
- DCL files don't allow infix notation.  If you were previously applying plugins like:

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
- NDOC discussion...
- Using the wrong annotation types
- Other stuff from my notes
- Unnecessary brackets are actually necessary...


TODO
- Continue expanding Footguns





