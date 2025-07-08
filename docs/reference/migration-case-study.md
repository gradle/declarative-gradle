# Case Study - Migrating Gradle-Client

## Introduction

In February and March 2025, using the version of Declarative Gradle available as EAP 3, we migrated the [`gradle client`](https://github.com/gradle/gradle-client) application to Declarative Gradle.
This migration included defining a custom Software Type to represent the plugins and tools used by one of its projects, and replacing all Kotlin buildscripts with DCL files.
Doing this gave us the opportunity to explore the DCL migration process in depth and identify common stumbling blocks and areas for improvement.

This document captures our experiences and presents them as a case study.
It highlights pain points, gotchas, and other insights surfaced by an actual migration.
You can view the results of the migration by comparing [the initial state of the repository](https://github.com/gradle/gradle-client/tree/8d5c4fefb10d7feae402fcae3106310a0495f535) with the [final post-migration state](https://github.com/gradle/gradle-client/tree/96d2b0adecfc9d622b77f1d67bbad33e8d752da3).

## Overview

The Gradle Client is a desktop application built with Kotlin Compose Desktop that uses the Gradle Tooling API to provide a rich GUI for analyzing and manipulating Declarative Gradle builds.
The particulars of what this application does when run aren’t especially relevant to the migration process, however.  

The project is structured as a multi-project build that comprises 3 Gradle subprojects:
- `:build-action` - a plain Java library 
- `:mutations-demo` - a Kotlin JVM library 
- `:gradle-client` - the main Kotlin JVM application

We generally followed [the migration guide](migration-guide.md) from our documentation.
However, if you review the repository commits, you'll notice we migrated the _most_ complex project first, which is the opposite of what the guide recommends.

We chose to start with the [`:gradle-client` subproject](https://github.com/gradle/gradle-client/tree/8d5c4fefb10d7feae402fcae3106310a0495f535/gradle-client), because it was the most important and feature-rich project.
Our goal was to explore the most interesting parts of the process early on.

For your own migrations, as noted in our guide, we recommend starting with simpler projects—those that closely resemble our declarative samples.
These are more likely to work with minimal effort and help build confidence before tackling complex parts of your build.

## Preparing for Migration

For the migration of the Gradle Client project, and to support the latest Declarative Gradle Configuration Language (DCL), we eventually upgraded the Gradle wrapper to version [gradle-8.14-milestone-4](https://services.gradle.org/distributions/gradle-8.14-milestone-4-bin.zip).
During the migration, we often used nightly snapshot versions to access DCL features that were still under active development.
By the time you read this, a later milestone or release candidate of Gradle 8.14 may be available for use in your own migrations.

The Declarative Gradle prototype includes a Kotlin Multiplatform (KMP) prototype plugin.
As the `:gradle-client` project is a Kotlin Multiplatform project (which only actually targets the JVM), we applied the corresponding Ecosystem Plugin in the root project's existing `settings.gradle.kts` file:

```
plugins {
    id("org.gradle.experimental.kmp-ecosystem").version("0.1.41")
}
```

Next, we renamed  `settings.gradle.kts` to `settings.gradle.dcl`.

Because the formats are similar for most common settings, the contents didn’t change significantly during the conversion.

You can compare the changes directly:
- [Resulting `settings.gradle.dcl` file](https://github.com/gradle/gradle-client/blob/96d2b0adecfc9d622b77f1d67bbad33e8d752da3/settings.gradle.dcl)
- [Original `settings.gradle.kts` file](https://github.com/gradle/gradle-client/blob/8d5c4fefb10d7feae402fcae3106310a0495f535/settings.gradle.kts)

A few changes stand out in the converted settings file:
- There’s no need for `@file:Suppress("UnstableApiUsage")`, this is done automatically for Declarative files
- We’ve added an included build and applied the `org.gradle.client.ecosystem.custom-ecosystem` plugin (more on these changes below)
- Content filtering of repositories isn’t available in DCL yet 
(this is likely to change in a future release, but as it is “merely” a performance optimization here, it can be omitted without impacting build correctness)
- `repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS` becomes `repositoriesMode = FAIL_ON_PROJECT_REPOS`, as DCL automatically understands enum types used in assignments
- The imperative check for Java 17 is removed
Declarative projects typically specify the JDK version they require as part of the project definition directly inside their Software Type, so this is no longer necessary

## Migrating Incrementally

Attempting to convert an entire build to DCL in one step is rarely practical except for the simplest builds.
Fortunately, migrations can be done incrementally, both across the build and within individual projects. 
Just like you can mix Groovy and Kotlin DSLs in a multi-project build, you can also mix declarative and non-declarative build scripts.
Inside each individual project’s buildscript, you can incrementally introduce declarative Software Types alongside imperative configuration logic.

To get started, we set the `org.gradle.kotlin.dsl.dcl=true` flag in the root project's `gradle.properties` file.
We could then add a Software Type to an existing build file and move configuration code into that Software Type piece-by-piece while deleting the corresponding code from the remaining “imperative” part of our build script outside its declaration.
This workflow made it easy to confirm correctness along the way, such as ensuring dependencies were still available for compilation after moving them into the Software Type.

This approach is also helpful because there is always a clear visual boundary between what parts of the build have already been “declarativized” and what parts are still pending.

## Using Existing Prototype Plugins

Core functionality, such as typical `implementation`, `api`, and test dependencies for library projects in common ecosystems, is already supported by the existing Software Types provided by Gradle's prototype plugins.
Migrating project dependencies declared in the top-level `dependencies` block to the dependency support provided by Software Types served as a good test of our DCL setup.

In the `gradle-client` build, the `:build-action` project was a straightforward candidate for migration. 
As a plain Java library, it was fully compatible with the existing `javaLibrary` prototype Software Type without any modifications.
The resulting `build-action/build.gradle.dcl` is simple and expressive:

```
javaLibrary {
    javaVersion = 8

    dependencies {
        implementation("org.gradle:gradle-tooling-api:8.14-milestone-4")
        implementation("org.gradle:gradle-declarative-dsl-tooling-models:8.14-milestone-4")
    }
}
```

Compare this with the original imperative version of `build-action/build.gradle.kts`:

```
plugins {
    java
}

dependencies {
    implementation(libs.gradle.tooling.api)
    implementation(libs.gradle.declarative.dsl.tooling.models)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}
```

The DCL version is more concise and communicates the same intented "project definition".
The main disadvantage is the use of hardcoded GAV (Group:Artifact:Version) strings for dependencies, rather than the type-safe references provided by the Version Catalog in the original. 
This is a temporary limitation, and support for shared version declarations is expected in a future EAP.

The migration of the `:mutations-demo` project is similarly straightforward.  
It uses Kotlin and was migrated using the `kotlinJvmLibrary` prototype Software Type:

The new `mutations-demo/build.gradle.dcl`:

```
kotlinJvmLibrary {
    dependencies {
        implementation("org.gradle:gradle-declarative-dsl-core:8.14-milestone-7")
    }
}
```

Replaces the original `mutations-demo/build.gradle.kts`:

```
plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    implementation(libs.gradle.declarative.dsl.core)
}
```

With no loss of clarity.

## Migrating Complex Projects

The existing prototype plugins were insufficient for a more complex project, such as the `:gradle-client` project (not to be confused with the overall build itself).

While `:gradle-client` is a Kotlin application (technically a KMP application that only targets the JVM), it uses features and plugins that fall outside the scope of the existing prototype plugins.
For example, SQL Delight, Detekt, Compose, and other functionality is not part of KMP, so support for this is not present.  

As a result, we could not simply apply ad configure the existing `kotlinApplication` Software Type without removing vital features from the project.
Like all our prototype declarative plugins, that plugin does not yet support composition or extensibility, features that would allow combining functionality like `kotlinApplication` with SQLDelight or Detekt in a modular way.

Support for Composition and Extensibility is a highly requested and essential feature for the DCL. 
It is actively in development and expected in a future EAP.

The approach we used for this project is the one outlined in our [Migration Guide](migration-guide.md): 
We setup an included build to define a new plugin.
This plugin implements a new Software Type that contains built-in support for _all_ the specific features used by this project.
We also defined a new Ecosystem Plugin that exposes this Software Type so that we can use it for projects in our build.

### Setting up the Included Build

As explained in the Migration Guide, we defined our custom plugins using an included build instead of `buildSrc`. 
This is because `buildSrc` is built after the project's settings script is evaluated, so it can't contain a `Plugin<Settings>` to be used by the build.
Our Ecosystem Plugin will be implemented as such a settings plugin.

We created a new `build-logic` directory and added the following to our build's root `settings.gradle.kts`:

```
pluginManagement {
    includeBuild("./build-logic")
    // ... other configuration
}
```

Then, we added a `build-logic/settings.gradle.dcl` file and applied the `org.gradle.experimental.plugin-ecosystem` plugin to it:

```
plugins {
    id("org.gradle.experimental.plugin-ecosystem") version("0.1.41")
}
```

This allows us to use the `javaGradlePlugin` plugin, which is necessary for _declaratively_ writing Gradle plugins—like the custom Software Types we’re about to create here.


Next, we created a `build-logic/plugins` subproject that contains our new Software Types.  
We included the project in the included build’s `settings.gradle.dcl` as usual.
In `build-logic/plugins`, we created a `build.gradle.dcl` file with an empty top-level `javaGradlePlugin {}` block. 
This is where we will register our new plugins.

While we could have registered the plugins directly in the root project of the `:build-logic` build, this extra level of separation could be useful if you have other imperative logic to migrate that wouldn’t be entirely contained in new Software Types.

This structure allows for:
- Creating other plugins, such as other settings plugins that provide additional task types or global configuration that you could then apply to your build.
- Keeping different types of logic isolated from the projects that produce your Software Type plugins.

### Writing a New Software Type Plugin

An initial idea for implementing our custom Software Type might be to extend an existing one like `kotlinApplication`, to take advantage of support for dependencies and SDK versioning already implemented there. 
However, due to a technical limitation, each Software Type plugin can expose only one software type.
So we'll have to create a brand new plugin.

The project we’re migrating is a Compose Desktop application written in Kotlin.
To support it declaratively, we’ll define a custom Software Type plugin tailored to its needs. 
In `build-logic/plugins/src/main/java`, we begin by adding a new plugin class: `org.gradle.client.softwaretype.CustomDesktopComposeApplicationPlugin` that implements `Plugin<Project>` and has a `public void apply(Project)` - just like every Gradle project-based plugin.

The `build-logic/plugins/src/main/java/org/gradle/client/softwaretype
/CustomDesktopComposeApplicationPlugin.java` file begins like this:


```
package org.gradle.client.softwaretype;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public abstract class CustomDesktopComposeApplicationPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {}
}
```

The name of the Software Type class itself does not need to be concise since it is not visible to consumer builds.
What the consumer will see is the name of the declarative extension exposed by the plugin. 
For clarity and usability, we chose a short but descriptive extension name: `desktopComposeApp`.

To support configuring our Software Type, we need to define a model interface (i.e., the extension type) that backs it. 
This interface defines the DSL that consumers will use declaratively.

In `build-logic/plugins/src/main/java/org/gradle/client/softwaretype/CustomDesktopComposeApplication.java` we add an empty interface:

```
package org.gradle.client.softwaretype;

public interface CustomDesktopComposeApplication {}
```

That’s all we need to get started.

Next we need to connect the plugin class to the extension.
We do this by annotating a getter on the plugin class with `@SoftwareType`.

This gives us the following `build-logic/plugins/src/main/java/org/gradle/client/softwaretype/CustomDesktopComposeApplicationPlugin.java`:

```
package org.gradle.client.softwaretype;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.software.SoftwareType;

@SuppressWarnings("UnstableApiUsage")
public abstract class CustomDesktopComposeApplicationPlugin implements Plugin<Project> {
    public static final String DESKTOP_COMPOSE_APP = "desktopComposeApp";

    @SoftwareType(name = DESKTOP_COMPOSE_APP, modelPublicType = CustomDesktopComposeApplication.class)
    public abstract CustomDesktopComposeApplication getDesktopComposeApp();

    @Override
    public void apply(Project project) {}
}
```

We add `@SuppressWarnings("UnstableApiUsage")` to the plugin class to silence distracting IDE warnings related to the use of in-development Declarative API types.
It’s a good idea to add this annotation to most of the Java classes in your plugin so that other actionable warnings are more visible.

Note that we haven’t yet written a concrete class - only interfaces and abstract classes. 
The getter method is left abstract as well.
In our own migration, the only concrete types in the plugin project were static utility classes — which could have been abstract too.
This is expected when writing a Declarative Software Type plugin - it should rely on Gradle to instantiate everything automatically, including providing implementations for abstract methods like `getDesktopComposeApp()` that the plugin will use to access the data configured in the extension by a build using it in a DCL file.

At this point, we’ve completed the minimum setup needed to define a (currently empty) Software Type. 
All that remains is to package the plugin for use.

In `build-logic/plugins/build.gradle.dcl`, we add:

```
javaGradlePlugin {
    description = "Declarative plugins containing custom software types for the gradle-client project."

    registers {
        id("org.gradle.client.softwaretype.desktop-compose-application") {
            description = "A custom software type for the Gradle Client's desktop Compose application"
            implementationClass = "org.gradle.client.softwaretype.CustomDesktopComposeApplicationPlugin"
        }
    }
}
```

This is how we use the Declarative prototype version of the Java Gradle Plugin Plugin (provided by the prototype ecosystem) to publish our custom Software Type plugin.
At this point, the plugin is usable from any project in the Gradle Client build.

### Writing a New Ecosystem Plugin

In Declarative Gradle, you don’t apply plugins in project `build.gradle.dcl` files.
Why?
Because plugin application is imperative.

Telling Gradle to _apply_ a plugin is _build logic_ dealing with _how_ a build behaves, instead of defining _what_ a project represents.
Plugins contain imperative logic and are applied imperatively, so they do not belong in a project’s DCL file.

You can read more about Declarative Gradle design principles in our [blog post](https://blog.gradle.org/declarative-gradle-first-eap#developer-first-configuration).

To make a Software Type available to a project, you apply an Ecosystem Plugin.
An Ecosystem Plugin:
- Implements `Plugin<Settings>`
- Is applied prior to project configuration
- Tells Gradle about Software Type(s) that will be used by projects in a build

In our case, we create our Ecosystem Plugin in  `build-logic/plugins/src/main/java/org/gradle/client/ecosystem/CustomEcosystemPlugin.java`:

```
package org.gradle.client.ecosystem;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.plugins.software.RegistersSoftwareTypes;
import org.gradle.client.softwaretype.CustomDesktopComposeApplicationPlugin;


@RegistersSoftwareTypes({CustomDesktopComposeApplicationPlugin.class})
public abstract class CustomEcosystemPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings settings) {}
}
```

Although this is a complete implementation of a plugin, everything important is accomplished via the @RegistersSoftwareTypes annotation so the `apply(Settings)` method is left empty.
This is typical for Ecosystem Plugins, and in the future, we may streamline this boilerplate even further.

Next, we register the Ecosystem Plugin in `build-logic/plugins/build.gradle.dcl` by adding another entry to the `registers` block:
 
```
id("org.gradle.client.ecosystem.custom-ecosystem") {
    description = "A custom ecosystem plugin registering the Software Type plugins provided by this project"
    implementationClass = "org.gradle.client.ecosystem.CustomEcosystemPlugin"
}
```

That’s actually all that’s needed for our Ecosystem Plugin — its sole purpose is to register Software Type plugins so they can be used declaratively in the build.

It’s important to keep in mind that if our vision for Declarative Gradle succeeds then writing your own Ecosystem Plugins won’t be something you’ll need to do for every project.
Our long-term goal is to offer a large assortment of Ecosystem Plugins available for popular frameworks and project types.
Combined with our pending work on Composability and Extensibility, migrating to a Declarative build should feel more like ordering a multi-course meal from a curated menu than cooking everything from scratch yourself.

Before going any further, now is a good time to sanity check our setup by applying our new Software Type to the `:gradle-client` project.
Even though the plugin currently does absolutely nothing, that’s expected and is not a problem.
A Kotlin DSL buildscript can use Software Types alongside imperative logic – so for now, the imperative logic will handle 100% of the work. 
We’re simply putting the Software Type in place to verify the relevant wiring is correct before continuing.

In the root `settings.gradle.dcl` file, we apply the Ecosystem Plugin to the `plugins` block:

```
plugins {
    // ... other existing plugin declarations
    id("org.gradle.client.ecosystem.custom-ecosystem")
}
```

In the `gradle-client/build.gradle.kts` file, we add to following line to the bottom of the file:

```
desktopComposeApp {}
```

At this point, we should be able to build the project as usual.

While our Software Type isn’t doing anything yet, a successful build confirms that Gradle has properly detected and configured it.

### Beginning to Migrate The Project’s Configuration: Dependencies

We started our migration with dependencies. 
This is often the best place to begin — dependencies are foundational to any build, and it's relatively easy to verify their correctness.

To migrate dependencies to the new DSL, we need a location within the SoftwareType’s extension where they can be declared.
For example, in a typical Kotlin project (like `:mutations-demo`), you’d see:

```
kotlinJvmLibrary {
    dependencies {
        implementation("g1:a1:v1")
        implementation("g2:a1:v1")
        api("g2:a2:v1")
        // ... continued
    }
}
``` 

You can see this implemented in [the Kotlin prototype plugin](https://github.com/gradle/declarative-gradle/tree/v0.1.41/unified-prototype/unified-plugin/plugin-kmp/src/main/java/org/gradle/api/experimental/kmp/KmpLibrary.java), where the Software Type extends the `HasLibraryDependencies` interface defined in [HasLibraryDependencies.java]( https://github.com/gradle/declarative-gradle/tree/v0.1.41/unified-prototype/unified-plugin/plugin-common/src/main/java/org/gradle/api/experimental/common/HasLibraryDependencies.java):

```
public interface HasLibraryDependencies {
    @Nested
    LibraryDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super LibraryDependencies> action) {
        action.execute(getDependencies());
    }
}
```

The getter here returns a `LibraryDependencies` instance defined in [LibraryDependencies.java](https://github.com/gradle/declarative-gradle/tree/v0.1.41/unified-prototype/unified-plugin/plugin-common/src/main/java/org/gradle/api/experimental/common/LibraryDependencies.java):

```
public interface LibraryDependencies extends BasicDependencies {
    DependencyCollector getApi();
}
```

Which in turn extends `BasicDependencies` defined in [BasicDependencies.java](https://github.com/gradle/declarative-gradle/tree/v0.1.41/unified-prototype/unified-plugin/plugin-common/src/main/java/org/gradle/api/experimental/common/BasicDependencies.java):

```
public interface BasicDependencies extends Dependencies, PlatformDependencyModifiers {
    DependencyCollector getImplementation();
    DependencyCollector getRuntimeOnly();
    DependencyCollector getCompileOnly();
}
```

There is a degree of abstraction and reuse here that adds some complexity, but the core idea is simple.
In essence, to declare dependencies declaratively, we expose a nested extension that:
- Implements `org.gradle.api.artifacts.dsl.Dependencies`, and
- Provides one or more getters that return a `org.gradle.api.artifacts.dsl.DependencyCollector`

When Gradle processes a DCL (or Kotlin DSL) file and finds Software Types it looks within their extension classes for any `@Nested` types that implement `Dependencies`, and records the `DependencyCollector` getter methods.
This allows method calls like `implementation(<some coordinates string>)` or `api(project(<some project>))` to map correctly to overloads of the `DependencyCollector.add(...)` present on the collectors returned by those getters.
It may look like magic, but it’s well-supported, and tools like IDEs understand these conventions through the Gradle Tooling API.

#### Option 1: Create a new custom Dependencies type

To get dependencies working in our custom Software Type, we can define a simplified dependencies type and expose it from our plugin. 
We start by creating a new interface at `build-logic/plugins/src/main/java/org/gradle/client/softwaretype/CustomDependencies.java`:

```
package org.gradle.client.softwaretype;

import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers;

public interface CustomDependencies extends Dependencies {
    DependencyCollector getImplementation();
    DependencyCollector getRuntimeOnly();
    DependencyCollector getCompileOnly();
}
```

We expose this from the SoftwareType extension, in `build-logic/plugins/src/main/java/org/gradle/client/softwaretype/CustomDesktopComposeApplication.java`:

```
public interface CustomDesktopComposeApplication {
    @Nested
    CustomDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super CustomDependencies> action) {
        action.execute(getDependencies());
    }
}
```

Now in `gradle-client/build.gradle.kts`, you can declare dependencies using the new custom SoftwareType:

```
desktopComposeApp {
    dependencies {
        implmentation(“myGroup:myModule:myVersion”)
        compileOnly(“myGroup2:myModule2:myVersion2”)
    }
}
```

DCL files do not currently support Version Catalogs, which many modern projects rely on.
However, Kotlin DSL files (*.kts) _do_ support them.
So, if you’ve verified everything works in Kotlin DSL, you may need to manually replace Version Catalog references with inline strings to transition to a DCL file.

This can feel like a step backward, but it’s a known limitation. 
We're actively exploring solutions. 
In the meantime, AI tools can help: if you copy an existing Version Catalog and a dependencies block using it into one and ask it to inline the use of the catalog, the translations are often perfectly correct. 

At this stage we can declare dependencies using the new nested blocks, but these declarations don’t yet affect the build.
That’s because `DependencyCollector` does exactly what it says: it collects dependency declarations. 
It does not resolve them or apply them to a project. 
To make these dependencies functional, we must wire them into the appropriate `Configuration` that will resolve them.

You can see how this is done in the [prototype JVM plugin](https://github.com/gradle/declarative-gradle/tree/v0.1.41/unified-prototype/unified-plugin/plugin-jvm/src/main/java/org/gradle/api/experimental/jvm/internal/JvmPluginSupport.java#L50):


```
project.getConfigurations().getByName(sourceSet.getImplementationConfigurationName())
            .getDependencies().addAllLater(dependencies.getImplementation().getDependencies());
```

For every collector, we must find the appropriate resolvable `Configuration` and use `addAllLater` so that Gradle knows to lazily add the collected dependencies from that collector to that configuration during dependency resolution.

Once this wiring is in place, we can move dependency declarations from the imperative top-level `dependencies` block to the corresponding collector in our Software Type’s new nested `dependencies` block.
If everything is wired correctly, our build should continue to function identically after each dependency is moved.

#### Option 2: Reuse an existing Dependencies type

While creating a custom dependencies block is useful, many projects may not need to define one from scratch.
In fact, the existing `dependencies` blocks defined in our prototype plugins should be sufficient for most use cases.

If your build already includes a dependency on one of our prototype plugins (e.g., our included build’s `:plugins`): 

```
api("org.gradle.experimental.kmp-ecosystem:org.gradle.experimental.kmp-ecosystem.gradle.plugin:0.1.40")
``` 

Then you can reuse these types in the extensions you write for your own custom Software Types:

```
import org.gradle.api.experimental.common.BasicDependencies;
// ... more imports


public interface MyUniqueApplication {
    @Nested
    BasicDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super BasicDependencies> action) {
        action.execute(getDependencies());
    }
}
```

With this approach, wiring the `DependencyCollectors` to the corresponding configurations works exactly the same as described in Option 1. 

#### Option 3: Reuse an existing Software Type

Ultimately, we chose not to go with either of the previous options.
Instead, we opted to reuse the complete pre-existing `kotlinApplication` Software Type we had already built as part of the prototype plugin.

This approach allowed us to take advantage of more than just the `dependencies` block, we could also leverage the broader project configuration logic already implemented in the prototype.

### Reusing an Existing Software Type

To demonstrate our goal, we can undo the changes to the `gradle-client/build.gradle.kts` that introduced our custom Software Type, and instead explore how the existing prototype would work in its place.

By adding the following to the `/gradle-client/build.gradle.kts` file:

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

                // ...other dependency declarations
            }
        }
    }
}
```

and _removing_ those same dependencies from the old `jvmMain.dependencies` block we should end up with a fully working build.

This makes sense, as the `:gradle-client` project was a KMP project that only targets the JVM platform. 
Swapping out the declarations in `jvmMain.dependencies` for the `jvm.dependencies` block yields the same result, as the `kotlinApplication` Software Type handles wiring dependencies into the correct configurations.

The JDK version declaration is also required by this Software Type, so it is required. 
Setting it to 17 here removes the need for the imperative version check that previously livesd in the settings file. 

With this minimal setup, the prototype plugin now takes care of wiring these dependencies into the appropriate configurations, and the project continues to build and run as before.

This is exactly the kind of reuse we’re aiming for—not just dependency declarations, but for other logic already built into the prototype plugin, like the JDK version declaration. 

Now the question becomes: How can we reuse all of this logic in our custom `desktopComposeApp` type without duplicating the implementation?

#### Attempt 1: Inheritance

At first glance, this seems like an ideal use case for inheritance.
We already have a working  `kotlinApplication` and just want to extend it with custom functionality (like SQL Delight and Detekt).
The `StandaloneKmpApplicationPlugin` plugin class is even marked `abstract`, perhaps suggesting it is designed for inheritance.
So we’ll try the natural thing: subclass this plugin, override `apply(Project)` to add new logic, and call `super.apply(project)` to retain everything from the original.

Unfortunately, this approach fails from the start.

Each Software Type plugin can only expose a single Software Type.
The `StandaloneKmpApplicationPlugin` already exposes the `kotlinApplication` type via a getter annotated with `@SoftwareType`. 
If we try to add another getter annotated with `@SoftwareType` to this plugin or a subtype of it, Gradle throws an error like:

```
  > A problem was found with the DesktopComposeApplicationPlugin plugin.
      > Type 'org.gradle.client.softwaretype.DesktopComposeApplicationPlugin' is registered as a software type plugin, but it exposes multiple software types.
```

As of now, Declarative Gradle enforces a one-software-type-per-plugin rule. 
That may change in a future release, but for now, inheritance won’t work if your goal is to define a new Software Type based on an existing one.
We’ll need to find another way.

#### Attempt 2: Brute-Force Composition

Since we can’t use inheritance, we turned to composition instead.

The idea is simple, have our `desktopComposeApp` contain a nested `kotlinApplication` block.
This gives us access to all the existing functionality already defined in that Software Type like dependencies, target configurations, nested blocks, and more.
Once that’s working, we can incrementally add new nested blocks for the specific features (SQL Delight, Detekt) used by the `:gradle-client` project. 

To start, we added the nested extension to `CustomDesktopComposeApplication`:

```
public interface CustomDesktopComposeApplication {
    @Nested
    KmpApplication getKotlinApplication();

    @Configuring
    default void kotlinApplication(Action<? super KmpApplication> action) {
        action.execute(getKotlinApplication());
    }
}
```

This will result in new usable blocks (`kotlinApplication {}` inside `desktopComposeApp {}`) in our DCL file that do...absolutely nothing.

We needed to wire the nested block up so that it behaved like the original `kotlinApplication` Sotfware Type. 
That logic for that wiring lived inside the `StandaloneKmpApplicationPlugin`’s apply method. 
To reuse it, we first had to refactor that logic into a separate utility method that we could access.

We created a `PluginWiring` class with a public static `wirePlugin` method (later renamed to `wireKMPApplication`). 
This method accepted 2 arguments: the current `Project` and the nested extension interface we would configure in the DCL file. 

This allowed us to isolate the wiring of the nested type from applying its plugin and resue that logic.
[This commit](https://github.com/gradle/declarative-gradle/commit/1e9e66ffb59048b977de21b55f099f288f4cedd3) shows the change.

With that refactoring in place, our plugin became:

```
import static org.gradle.api.experimental.kmp.StandaloneKmpApplicationPlugin.PluginWiring.wireKMPApplication;

@SuppressWarnings("UnstableApiUsage")
public abstract class CustomDesktopComposeApplicationPlugin implements Plugin<Project> {
    public static final String DESKTOP_COMPOSE_APP = "desktopComposeApp";

    @SoftwareType(name = DESKTOP_COMPOSE_APP, modelPublicType = CustomDesktopComposeApplication.class)
    public abstract CustomDesktopComposeApplication getDesktopComposeApp();

    @Override
    public void apply(Project project) {
        CustomDesktopComposeApplication dslModel = getDesktopComposeApp();
        wireKMPApplication(project, dslModel.getKotlinApplication());
    }
}
``` 

After making these changes, we were able to move most of the “basic” logic of our KMP application to our new plugin using the nested `kotlinApplication` block.
[This commit](https://github.com/gradle/gradle-client/blob/e0b98007a8a1b337729b2df3fb40c80a0fa59e0e/gradle-client/build.gradle.dcl) reflects those changes.
If you’ve been following our earlier EAPs, much of this configuration should look familiar — it’s exactly what the prototype plugins supported before.
Once these declarations are moved into our new nested block, we can remove them from the imperative part of this file (everything that remains outside the `desktopComposeApp` block), and still have a buildable project.

It’s important to note that that is almost certainly *not* how composition will ultimately work in Declarative Gradle.
Our long-term goal is to make plugin composition first-class, flexible, and far less manual.
But for now, with EAP, this brute-force composition approach gets us the reuse we need without duplicating the logic we’ve already built.

### Wiring Additional Functionality from Plugins

This still leaves a significant portion of our application’s functionality unaccounted for — specifically, the “custom” parts: Compose, SQLDelight, and Detekt.

To address this, we gradually built support for each feature within our declarative desktopComposeApp Software Type. 
The goal is to migrate all functionality from the imperative parts of the build script into this unified declarative model until nothing imperative remains.
At this stage, there are multiple ways to approach the problem, and which one works best will depend on your specific needs and constraints.

Let’s start with something relatively simple: Detekt.

#### Supporting Detekt

When supporting a third-party plugin like Detekt, there are typically two steps involved:
1. We need to create a new nested block in our Software Type to hold configuration data for that plugin
2. We need to apply the plugin wiring that configuration data as needed

The existing block used to configure Detekt in the imperative buildscript is quite simple:

```
detekt {
    source.setFrom("src/jvmMain/kotlin", "src/jvmTest/kotlin")
    config.setFrom(rootDir.resolve("gradle/detekt/detekt.conf"))
    parallel = true
}
```

We [translated](https://github.com/gradle/gradle-client/blob/e0b98007a8a1b337729b2df3fb40c80a0fa59e0e/build-logic/plugins/src/main/java/org/gradle/client/softwaretype/detekt/Detekt.java) this into a simple extension type that uses `Property` types compatible with DCL:


```
public interface Detekt {
    @Restricted
    ListProperty<Directory> getSource();

    @Restricted
    ListProperty<RegularFile> getConfig();

    @Restricted
    Property<Boolean> getParallel();
}
```

This new Detekt block was added as a `@Nested` block inside our `CustomDesktopComposeApplication` Software Type:


```
@Nested
    Detekt getDetekt();

    @Configuring
    default void detekt(Action<? super Detekt> action) {
        action.execute(getDetekt());
    }
```

We’re not _recreating_ the exact types from the Detekt Gradle plugin, but modeling the inputs we need in a way that makes sense for our project and is compatible with the DCL.
That’s why we use lists of files and directories rather than `ConfigurableFileCollection` for the source and configuration files.
We fully utlize Gradle’s Lazy Provider APIs and wrap each data type in the appropriate type of `Property`.

If you compare the before and after commits for the `:gradle-client` project’s build script, you can see how the Detekt configuration was migrated from the original imperative style to the new declarative form nested under the `desktopComposeApp` block.

```
detekt {
    source = listOf(layout.projectDirectory.dir("src/jvmMain/kotlin"), layout.projectDirectory.dir("src/jvmTest/kotlin"))
    config = listOf(layout.settingsDirectory.file("gradle/detekt/detekt.conf"))
    parallel = true
}
```

In general, creating new extensions like this is straightforward. 
You can reference the existing DSL to see which values are being set, then define matching `Property` getters and configure methods with the appropriate DCL annotations.

At this point, though, builds don’t actually run Detekt yet. We’ve only modeled the configuration — we still need to wire this extension to the plugin.

To keep our code organized, we followed the pattern used for wiring the KMP Application support and created a new dedicated support class `DetektSupport`. 
[Here’s the `wireDetekt` method](https://github.com/gradle/gradle-client/blob/e0b98007a8a1b337729b2df3fb40c80a0fa59e0e/build-logic/plugins/src/main/java/org/gradle/client/softwaretype/detekt/DetektSupport.java):

```
public static void wireDetekt(Project project, CustomDesktopComposeApplication projectDefinition) {
        project.getPluginManager().apply("io.gitlab.arturbosch.detekt");

        project.afterEvaluate(p -> {
            Detekt detektDefinition = projectDefinition.getDetekt();
            DetektExtension detekt = project.getExtensions().findByType(DetektExtension.class);
            assert detekt != null;
            detekt.getSource().from(detektDefinition.getSource());
            detekt.getConfig().from(detektDefinition.getConfig());

            // This is not a property, need to wire this in afterEvaluate, so might as well wait to wire the rest of detekt with it
            detekt.setParallel(detektDefinition.getParallel().get());
        });
    }
```

We apply the Detekt plugin programmatically, which means it must be available on the classpath when our Software Type is applied.
To make that possible,, we add it as an `api` dependency in our plugin’s [buildscript](https://github.com/gradle/gradle-client/blob/96d2b0adecfc9d622b77f1d67bbad33e8d752da3/build-logic/plugins/build.gradle.dcl):

```
javaGradlePlugin {
    dependencies {
        // ... other deps
        api("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.6")
    }
    // ... other configuration
}
```

With this in place, applying our custom `desktopComposeApp` Software Type gives the project full Detekt support—with the configuration fully migrated to the declarative model.

##### The Need for `afterEvaluate`

One key detail in our wiring method is the use of `Project.afterEvaluate` to wrap the wiring of the data in our new extension (`Detekt detektDefinition`) to the Detekt plugin’s extension (`DetektExtenstion detekt`).

If you look at the `setParallel` method on the `detekt` instance (the Detekt plugin’s extension) and you’ll notice it only accepts a boolean value.
There is no overloaded that accepts a `Property<Boolean>`, which is what we’ve used to model this value in our DCL extension.
To work around this, we need to retrieve the value of our lazy `Property` via `get()` and call `setParallel()` using the result - but this can only be done once the model has been populated with data configured in the DCL.

Since the DCL runtime applies the plugin that configures the software type first and only then fills the model with the data from the `build.gradle.dcl` file, the plugin application code (starting from the `apply` method) cannot rely on the full model content being present at that time. 
The code configuring the plugin will not be able to eagerly access the final values in the model’s Java bean properties and plain collections at that point and will have to wait until later for them to be present.
Using `afterEvaluate` delays our access to this data until Gradle is ready to provide them.
This is a necessary workaround at the moment and a common pattern when building Declarative Software Types that use 3rd party plugins.

Because we need to delay setting the parallel value, we wrapped all of the Detekt configuration in `afterEvaluate` (even the source and config `FileCollection` properties, which are amenable to lazy wiring - note there is no call to `get()` involved in the DSL types here).
When reading this code, keep in mind that as the comment explains, this is only _necessary_ when a plugin does not make use of Gradle’s Lazy Provider API.

With this logic in place, we can now simply call our `wireDetekt` method from our custom Software Type plugin’s `apply` method, just like we did for `wireKMPApplication` — creating a consistent and maintainable pattern across plugin integrations.

#### Supporting SQLDelight

Supporting SQLDelight follows the same process we used for Detek, with one key difference: the SQLDelight plugin models its configuration using a `NamedDomainObjectContainer` (NDOC).

Let’s look at the original DSL used to configure SQLDelight:

```
sqldelight {
    databases {
        create("ApplicationDatabase") {
            packageName = "org.gradle.client.core.database.sqldelight.generated"
            verifyDefinitions = true
            verifyMigrations = true
            deriveSchemaFromMigrations = true
            generateAsync = false
        }
    }
}
```

Even at a glance, it's clear that the `sqldelight` extension defines a container named `databases` which provides a `create` method to add and configure new elements.

To replicate this structure in our DCL-based Software Type, we model the databases block using a `NDOC`.
This allows us to preserve much of the original DSL structure, now nesting a `sqlDelight` block inside our `desktopComposeApp` top-level block:

```
sqlDelight {
    databases {
        database("ApplicationDatabase") {
            packageName = "org.gradle.client.core.database.sqldelight.generated"
            verifyDefinitions = true
            verifyMigrations = true
            deriveSchemaFromMigrations = true
            generateAsync = false
        }
    }
}
```

Note that by default, an `NDOC` named `databases` will assume the method used to create a new element is called `database`. 
This can be customized, but the default behavior is sufficient for our needs.
We kept the name `database` instead of using `create`, because it sounds more declarative and aligns better with the style we’re aiming for than a method called `create`, which has imperative conotations.

The DCL model for the sqlDelight block is simple:

```
public interface SqlDelight {
    NamedDomainObjectContainer<Database> getDatabases();
}
```

Notice that the `NDOC` doesn’t require any special annotation - Declarative Gradle understands how `NDOC`s work and handles them automatically when parsing the model.

A database is a simple container that holds the properties we use to configure our SQLDelight database.
Though it is a fully abstract interface, it can be thought of as a simple value type - it exists to hold information about each database in our container:

```
public interface Database extends Named {
    @Restricted
    Property<String> getPackageName();

    @Restricted
    Property<Boolean> getVerifyDefinitions();

    @Restricted
    Property<Boolean> getVerifyMigrations();

    @Restricted
    Property<Boolean> getDeriveSchemaFromMigrations();

    @Restricted
    Property<Boolean> getGenerateAsync();
}
```

As with Detekt, we also need to wire our DCL configuration to the actual SQLDelight plugin at runtime.
The wiring is handled in another [support class](https://github.com/gradle/gradle-client/blob/e0b98007a8a1b337729b2df3fb40c80a0fa59e0e/build-logic/plugins/src/main/java/org/gradle/client/softwaretype/sqldelight/SqlDelightSupport.java):

```
public static void wireSqlDelight(Project project, CustomDesktopComposeApplication projectDefinition) {
        /*
         * It's necessary to defer checking the NDOC in our extension for contents until after project evaluation.
         * If you move the check below outside of afterEvaluate, it fails.  Inside, it succeeds.
         * Without the afterEvaluate, the databases is seen as empty, and the plugin fails, with this warning:
         * https://github.com/plangrid/sqldelight/blob/917cb8e5ee437d37bfdbdcbb3fded09b683fe826/sqldelight-gradle-plugin/src/main/kotlin/app/cash/sqldelight/gradle/SqlDelightPlugin.kt#L112
         */
        project.afterEvaluate(p -> {
            if (needToWireSqlDelight(projectDefinition)) {
                project.getPluginManager().apply("app.cash.sqldelight");

                projectDefinition.getKotlinApplication().getTargets().jvm(jvmTarget -> {
                    jvmTarget.getDependencies().getImplementation().add("app.cash.sqldelight:runtime:2.0.2");
                    jvmTarget.getDependencies().getImplementation().add("app.cash.sqldelight:coroutines-extensions:2.0.2");
                    jvmTarget.getDependencies().getImplementation().add("app.cash.sqldelight:sqlite-driver:2.0.2");
                });

                SqlDelightExtension sqlDelight = project.getExtensions().getByType(SqlDelightExtension.class);
                projectDefinition.getSqlDelight().getDatabases().forEach(dslModelDatabase -> {
                    sqlDelight.getDatabases().create(dslModelDatabase.getName(), database -> {
                        database.getPackageName().set(dslModelDatabase.getPackageName());
                        database.getVerifyDefinitions().set(dslModelDatabase.getVerifyDefinitions());
                        database.getVerifyMigrations().set(dslModelDatabase.getVerifyMigrations());
                        database.getDeriveSchemaFromMigrations().set(dslModelDatabase.getDeriveSchemaFromMigrations());
                        database.getGenerateAsync().set(dslModelDatabase.getGenerateAsync());
                    });
                });
            }
        });
    }
```

The `needToWireSqlDelight` method simply checks if `getDatabases().isEmpty()`.
Initially, we wanted our wiring logic to avoid using `project.afterEvaluate` at all until _after_ calling this method to check for databases in the container.
We thought: Why add an extra `project.afterEvaluate` callback unless we know we’re actually making use of SQLDelight by adding a database to the container in our extension?
Because the contents of the `NDOC` are only available _after_ project evaluation, this won’t work.
Assuming they will be ready earlier is an easy mistake to make when using an `NDOC`.

Other than this wrinkle, the rest of the wiring code should look familiar.
Since the SQLDelight plugin uses lazy Properties, we call `set(Provider)` and supply our extension's properties _without_ calling `get()` to realize their values.
This is how we envision most wiring working when the Provider API is present on both the plugin we are configuring and the declarative extension holding the values we are configuring it from.

In addition to applying the plugin and configuring its extension, we add the runtime dependencies needed by SQLDelight to the implementation configuration of the project’s JVM target.

#### Supporting Compose

Supporting Compose follows same foundational process we used for Detek or SQLDelightt. 
However, the Compose plugin’s configuration introduces significatly more complexity.
To support this, we’ve used multiple levels of nesting to build a DSL that simplifies some of the Compose plugin’s extensions but is, in some ways, limited by our DCL's current feature set. 

But designing Compose support for our `desktopComposeApp` Software Type still remains the same core process:
1. Build new DSL extensions to hold the configuration data needed by the plugin
2. Move the configuration from the imperative part of our buildscript into the new blocks using the property types
3. Write code to be called by our plugin during application to wire values from our DCL extension into the (Compose) plugin’s extension in an `afterEvaluate` closure

You can see the original Compose DSL configuration in the imperative buildscript [before migration](https://github.com/gradle/gradle-client/blob/b498fd896d858aecf4403ab077a4ea14bcd484d0/gradle-client/build.gradle.kts) and the resulting DCL DSL [after migration](https://github.com/gradle/gradle-client/blob/e0b98007a8a1b337729b2df3fb40c80a0fa59e0e/build-logic/plugins/src/main/java/org/gradle/client/softwaretype/detekt/DetektSupport.java).
Rather than walk through the full implementation line-by-line (which closely mirrors the structure of the Detekt and SQLDelight support), we’ll highlight a few notable aspects of our Compose support.

##### Simulating Map Support with `NDOC`s

One element in the Compose DCL block that might catch your eye is the `jvmArgs` section:

```
jvmArgs {
    jvmArg("-Xms") {
        value = "35m"
    }
    jvmArg("-Xmx") {
        value = "128m"
    }

    // This was originally added at an inner nesting level, but it's not clear why
    jvmArg("-splash") {
        value = ":\"\$APPDIR/resources/splash.png\""
    }
}
```

When you look at the [Compose extension](https://github.com/gradle/gradle-client/blob/e0b98007a8a1b337729b2df3fb40c80a0fa59e0e/build-logic/plugins/src/main/java/org/gradle/client/softwaretype/compose/Compose.java), you’ll see that this block is modeled using an `NDOC`. 
That might seem like a strange choice, given that pairs of JVM argument names and their corresponding values are a natural fit for a Map.
Unfortunately, native `Map` support was not available at the time of EAP3 (it is targeted for EAP4).

So if you’re looking at this bit of code and thinking it’s more complex than it should be — don’t worry, we feel the same.
Once map support lands in a future EAP, we plan to revisit this and simplify the structure considerably.

##### BuildTypes Simplified

An area that might give the opposite impression is the support for configuring the `release` build type:

```
buildTypes {
    release {
        proguard {
            optimize = false
            obfuscate = false
            configurationFiles = listOf(layout.projectDirectory.file("proguard-desktop.pro"))
        }
    }
}
```

Behind the scenes, this isn’t modeled as a container of user-defined build types (like you might see with NDOCs elsewhere) by the 3rd party Compose plugin.
Instead, it's implemented using several static nested extensions.
That’s because the `org.jetbrains.compose.desktop.application.dsl.JvmApplicationBuildTypes` extension provides a static list of methods that expose predefined build types such as release.
You can see this in our plugin's support code [on this line](https://github.com/gradle/gradle-client/blob/e0b98007a8a1b337729b2df3fb40c80a0fa59e0e/build-logic/plugins/src/main/java/org/gradle/client/softwaretype/compose/ComposeSupport.java#L52) where we call the parameterless `buildTypes.getRelease()` method directly. 

### Finish Declarativizing

With support for Detekt, SQLDelight, and Compose fully modeled and wired into our custom Software Type, we’re able to migrate all remaining configuration from the imperative parts of our buildscripts into the declarative `desktopComposeApp` block.

At this point, we can rename the buildscripts from `build.gradle.kts` to `build.gradle.dcl` and run the build again to verify that everything has been properly wired.

If this build succeeds, the declarative migration is complete. 

We can move on to end-to-end verification of our project—running the project and confirming that all functionality still works as expected. 

## Limitations and Unmigrated Functionality

The biggest compromise we currently face when supporting features from 3rd party plugins is the reliance on `afterEvaluate`.
Ideally, plugin authors would use Gradle’s lazy Provider API for configuration. That would allow us to connect the lazy properties in our declarative extensions directly to the plugin’s own extension properties immediately when we apply a plugin.
Unfortunately, most plugins don’t yet support this, so we’re often forced to wire things up in `afterEvaluate` once all the values set in buildscripts are available programmatically. 
We hope this situation improves in the future, and we’re actively exploring more elegant solutions through our ongoing Composability and Extensibility work.

In a more mature Declarative Gradle ecosystem, we envision finding existing Software Type Feature plugins for Detekt, Compose, and SQLDelight. 
These could be composed together with the existing Kotlin ecosystem plugin to create the equivalent of our `desktopComposeApp` with a minimum of coding.

The original project used a `compose` object, provided by the compose plugin, to access dependency declarations related to the Compose framework.
Using this object isn’t possible in DCL, because it is not available early enough to be understood when parsing the schema.
Instead, we determined which coordinates it was requesting and wrote them out as standard GAV strings to use in our dependency declarations.

One limitation is the inability to add additional imports, which prevents us from calling static methods on types where the static method is not already annotated as `@Restricted`.
This prevented us from modeling the `copyright` property using a `Property<Year>` and setting it in the DCL like:

```
copyrightYear = Year.now()
```

This limitation prevented us from modeling values like [this one](https://github.com/gradle/gradle-client/blob/b498fd896d858aecf4403ab077a4ea14bcd484d0/gradle-client/build.gradle.kts#L115) as cleanly as we’d like.
We're exploring ways to address this in future releases.

## Conclusion

There's no “easy button”, but as this case study shows, if you already know how to write Gradle Plugins, it's not _that_ hard to start using DCL in a real-world build.
And the benefits of moving toward a declarative Gradle paradigm are significant.

Shaping your DSL to match the structure of the plugins you want to wire it to is, at this stage, still more of an art than a science.
Building an effective DSL requires tradeoffs and thoughtful modeling.
But with the setup outlined here and in the migration guide, it’s easy to experiment, iterate, and quickly prototype something that works for your build.

Once your included build is fully set up, development is smooth and ergonomic.
It’s satisfying to add a new property to your Software Type, hit refresh, and immediately get type-safe support and IDE feedback. 
You can test the wiring with a single build; and when it works, you know it’s wired correctly.

Future Declarative Gradle EAPs will continue improving this process. 
We’re actively working on filling in missing features and delivering a powerful new model for Composability and Extensibility. 

Stay tuned!
