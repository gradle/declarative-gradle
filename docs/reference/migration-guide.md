# Migrating Existing Builds to Declarative Gradle

## Introduction

This page provides an overview of how to update existing builds to use Gradle's new Declarative Configuration Language (DCL).

As of the latest release (EAP 3) available at the time of writing (early April 2025) DCL's support for composability and extensibility is not yet available. 
The approach outlined here should be considered temporary and not the final state of the migration process.
We recognize that there are major areas for improvement, and we are actively exploring changes we hope to make available in the next EAP release.
However, despite these limitations, we think this guide will be useful to early adopters who are eager to experiment with Gradle’s new DCL _now_.

This guide is meant as an overview of a typical migration process.
For a detailed exploration of an actual migration, see the [Migration Case Study](migration-case-study.md).

## Migration Process

The important major steps of the migration process are outlined below.

### 1. Decide which projects to migrate first

In a multi-project build, some projects will likely be much easier to convert to DCL than others.
Projects with minimal imperative logic, that use fewer third-party plugins, and do typical, idomatic configuration in the buildscript, are easier to convert.
Projects that resemble our [Declarative Samples](https://declarative.gradle.org/docs/getting-started/samples/), or the output of Gradle's built-in [build init plugin](https://docs.gradle.org/current/userguide/build_init_plugin.html) are strong candidates to migrate first.
Starting with simpler projects allows you to verify that Declarative Gradle is properly setup, and makes debugging issues easier.

Keep in mind that using Declarative Gradle is **not** an all or nothing process.
Gradle supports mixing DSLs within a multi-project build — Groovy and Kotlin DSLs can coexist, and so can DCL.
This allows you to migrate a build incrementally, project by project.

### 2. Prepare the project for Declarative Gradle

If the project you are converting _doesn't_ already use Gradle's Kotlin DSL, begin by converting it using the official [Groovy to Kotlin DSL migration guide](https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html).
Also ensure you are using the latest stable Gradle version, at minimum [the current version used by the Declarative Gradle prototype plugins](https://github.com/gradle/declarative-gradle/blob/main/unified-prototype/gradle/wrapper/gradle-wrapper.properties). 

Next, enable Software Types in Kotlin DSL scripts by setting the `org.gradle.kotlin.dsl.dcl=true` flag in the root project's `gradle.properties` file.
This flag allows Gradle to understand Software Types used in `*.kts` buildscripts alongside imperative project configuration logic.

Finally, identify which declarative _Ecosystem Plugins_  are needed to expose the Software Types relevant to your project. 
For example, apply the [`jvm-ecosystem` plugin](https://plugins.gradle.org/plugin/org.gradle.experimental.jvm-ecosystem) if you’re working with JVM projects.
For more information, see the [Software Features reference](https://declarative.gradle.org/docs/reference/software-features/) in the Declarative Gradle documentation.

### 3. Convert your settings files to use DCL

First, rename your `settings.gradle.kts` file to a `settings.gradle.dcl` file.
This may require you to comment out or relocate any features that are not currently supported by the DCL.
For example, one commonly used feature that is not yet supported in DCL is [repository content filtering](https://docs.gradle.org/current/userguide/filtering_repository_content.html#sec:declaring-content-repositories). 
Such logic must be moved into project-level build scripts if it is necessary to continue to use it (this particular feature is usually an optional performance optimization).

Next, apply the required ecosystem plugins using a `plugins` block in the settings file - add one if it doesn't already exist.
Then run your build to ensure everything still works correctly.

### 4. Convert simple projects to use DCL

Once you’ve setup Declarative Gradle in settings file like this, you can begin migrating individual projects using a gradual, low-risk approach. 
Declarative Gradle supports mixed builds – using `*.dcl` buildscripts alongside traditional Groovy or Kotlin DSLs.
There is no need to migrate everything at once.

Start with the simplest projects that can be nicely described by existing declarative Software Types, such as [Gradle's prototype ecosystem plugins](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype).

Software Types can be used in Kotlin DSL buildscripts once the appropriate Ecosystem Plugin is applied in your settings file. 
You can add a new block to the bottom of your `build.gradle.kts` file using the `modelPublicType` from the `@SoftwareType` getter of the type you will be using.
For example:

```
plugins {
  // ... existing plugins used
}

// ... other imperative configuration

mySoftwareType {
  // ... declarative configuration
}
```

Depending on the defaults and conventions implemented in this Software Type, your project's configuration, and how they may conflict, your project may or may not continue to be buildable at this point.

Once the appropriate Ecosystem Plugin is applied in your settings, begin migrating configuration into the Software Type’s extension.
For example, any `dependencies` declared by your project in Gradle’s top-level `dependencies` block be moved to the equivalent location provided by your Software Type’s extension.

Fully configure the Software Type by: 
- Adding all dependencies
- Setting version information
- Providing necessary directory values
- Assigning any other property values needed

You may need to comment out or relocate any features that are not currently supported by the DCL or the Software Type in use.
Starting with simple projects helps reduce the likelihood of hitting these limitations early in the migration process.

For a detailed walkthrough, see our [Case Study](migration-case-study.md) which demonstrates this approach in a real multi-project build. 

Once all configuration has been moved into the Software Type and the project builds successfully:
- Rename the file from `build.gradle.kts` to `build.gradle.dcl`
- Verify your build again

Congratulations, you’ve now successfully migrated a project to Declarative Gradle.

Continue migrating your simpler projects one at a time. 
Stop when you reach a project that is too complex to migrate cleanly.
This can easily happen due to 3rd party plugins or custom build-logic.  
You can revisit options here as DCL support continues to expand.

### 5. Setup a Composite build

As you migrate more complex projects, you may find that existing Software Types do not cover all your configuration needs. 
It may be necessary to create custom Software Types to support advanced or project-specific features.
To support this, set up a [Composite build](https://docs.gradle.org/current/userguide/composite_builds.html#defining_composite_builds) for rapid development of declarative plugins.

A composite build allows you to include an additional build — often called an "included build" due to the method used to register it in your settings file.  
This included build is a fully independent build that produces products which your main build can automatically use.
Use this included build to define and test custom declarative plugins, including new Software Types tailored to your needs that can be used by the build you are migrating. 
The composite build makes it easy to iterate on these custom types as your migration progresses.

See the [Gradle documentation on composite builds](https://docs.gradle.org/current/userguide/composite_builds.html#defining_composite_builds) for further instructions.

### 6. Create custom Software Types

Once you’ve set up an included build, you can use it to define custom declarative plugins for your complex projects.
Start by creating a new project in the included build to hold your plugins. 
This project itself can also use Declarative Gradle.

Create a `settings.gradle.dcl` file in the root of your included build and apply the Gradle Plugin Ecosystem plugin in it:

```
plugins {
    id("org.gradle.experimental.plugin-ecosystem").version("0.1.41")
}
```

This Declarative Gradle plugin allows you to create new Software Types in Java.

See the [Software Features reference](https://declarative.gradle.org/docs/reference/software-features/) for more information on how to implement Software Types.
It's also helpful to review [the prototype plugins](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/unified-plugin) used to build the current test projects used in development.

A Software Type plugin requires at least 4 parts:
1. A **top-level DSL interface** that will be configured in the DCL file by any project using this Software Type
2. A **Software Type plugin** that implements `Plugin<Project>` and has a `public abstract` getter annotated with `@SoftwareType` exposing the DSL interface
3. An **"Ecosystem" plugin** that implements `Plugin<Settings>` and registers the software type via `@RegistersSoftwareTypes(MySoftwareTypePlugin.class)`
4. **Registration of the new plugins** in the `build.gradle.dcl` file of the project that contains them using the `registers` block provided by the `javaGradlePlugin` Software Type's top-level extension 

How you implement these parts depends on the specific needs of the projects you are targeting, what plugins they use, and how they are configured.

For a detailed example of this process, check out [a mini-"Case Study"](migration-case-study.md).
As a starting point, consider copying a simple prototype like the `jvm-library` Declarative plugin.

### 7. Convert complex projects to use DCL

Once you've created a custom Software Type, you're ready to apply it to more complex projects.

Start by:
- Adding your new Ecosystem Plugin to the list of plugins in your `settings.gradle.dcl` file
- Adding your new Software Type block to your project's existing `build.gradle.kts` file
- Configuring the new Software Type in the same way as you did with your simpler projects using pre-existing prototype Declarative plugins, by moving things like version and dependency declarations from the imperative parts of your buildscript, to the declarative part inside of your new Software Type

This process is iterative. 
You’ll migrate a piece of configuration, then realize the Software Type is missing some capability. 
Update the plugin in your included build, test again, and repeat.
This feedback loop is fast, since your included build and project are part of the same composite build, so you can test both together with a single build invocation.

Validating your build frequently after each change is highly recommended.
Before migrating any configuration, you should verify that your custom Software Type is correctly wired into the build:
- Create an empty type that does absolutely nothing
- Use it in your existing project’s `*.kts` file 
- Confirm it does not affect your build (unlike using the pre-existing prototype plugins, which may have requirements or clash with your project’s conventions)

This helps ensure that the integration is sound before you begin migrating real logic.

You should save your project before attempting to migrate larger bits of configuration, such as the application of each 3rd party plugin, into your Declarative Software Type plugin’s logic.

Once all logic has been moved into your Software Type and the imperative part of the build script is empty:
- Rename the file to `build.gradle.dcl`
- Verify your build one last time

### 8. Continue until all projects are using DCL

Once you’ve successfully set up your included build and applied your first custom Software Type to a project, you can continue migrating the remaining projects that require customization using the same approach.

Repeat the process—refining or extending your Software Types as needed—until every project in your build uses a declarative build script (`*.dcl` file).

When all the projects in your build are fully declarative, and every buildscript files present is a `*.dcl` file (meaning there are no remaining `*.kts` or `*.gradle` files), remove the `org.gradle.kotlin.dsl.dcl=true` flag from your `gradle.properties`.

### 9. (Optional) Extract common project configuration to defaults

Once your build is fully migrated, you can reduce duplication by extracting common configuration to the `defaults` block in your root `settings.gradle.dcl` file.
If you find that certain properties are always set to the same value across multiple projects using the same Software Types (for example, setting the same JDK version in every library project), you can configure this just once in `defaults` rather than repeating it in each build script.

You can see an example of this pattern in [the androidLibrary defaults block](https://github.com/gradle/declarative-gradle/blob/v0.1.44/unified-prototype/settings.gradle.kts#L28) in the Declarative Gradle prototype plugins repository.

## Hints, Tips and Footguns

Add `@file:Suppress("UnstableApiUsage")` to the top of your KTS files during the intermediate stages to silence IDE warnings about many of the DCL types being `@Incubating`.

DCL does not yet support Version Catalogs, so if you're using one, you'll need to convert your `libs.my.lib` dependency declarations to GAV coordinates like `org:mylib:1.4`.  
These are supported within Kotlin DSL (`*.kts`) buildscripts using Software Types but **not** within fully declarative `*.dcl` files.
This is annoying, but temporary until a better solution to reusable version declarations in DCL is made available in a future EAP.
If your organization allows it, your AI assistant of choice is likely very able to do this conversion if you provide your Version Catalog file and dependencies block, and ask it to produce the same dependencies using inline GAV coordinates no longer referencing the catalog file.

DCL files don't allow infix notation.  If you were previously applying plugins like:

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

When configuring Software Types, you may find yourself needing to use explicit empty pairs of braces (`{}`), especially when adding new elements to `NamedDomainObjectContainer`s, even if it is not necessary to perform any configuration on these new elements. 
This is due to limitations in our DCL parser, and may be addressed in a future version; for now, these empty pairs of braces remain necessary to avoid confusing parsing errors.

The names of the annotation types used by Declarative Gradle are fairly common.
Make sure you are using the proper DCL annotations:

```
org.gradle.api.tasks.Nested;
org.gradle.declarative.dsl.model.annotations.Configuring;
org.gradle.declarative.dsl.model.annotations.Restricted;
```

You might think to avoid some of the complexity of using an included build by making use of `buildSrc` to define your new plugins, especially if your build already uses `buildSrc` for other purposes.  
This will ***not** work for your Ecosystem Plugins.
`buildSrc` is built by Gradle only _after_ your build’s settings script is evaluated, so you can’t put `Plugin<Settings>`s in there and expect to be able to apply them in your `settings.gradle.kts/dcl` file.  
You need to use an included build (or an external dependency) to define the plugins you want to use for this.

Many DCL error messages are not yet polished and can be confusing.
For example:

If you use `@Restricted` instead of `@Nested` on an inner block type, you get unhelpful messages like:

```
org.gradle.api.internal.plugins.PluginApplicationException: Failed to apply plugin class 'org.gradle.client.softwaretype.CustomDesktopComposeApplicationPlugin'.
...
Caused by: org.gradle.internal.instantiation.ClassGenerationException: Could not generate a decorated class for type CustomDesktopComposeApplication.
...
Caused by: org.gradle.api.reflect.ObjectInstantiationException: Could not create an instance of type org.gradle.client.softwaretype.CustomDesktopComposeApplication.
...
Caused by: java.lang.IllegalArgumentException: Cannot have abstract method CustomDesktopComposeApplication.getCompose().
```
