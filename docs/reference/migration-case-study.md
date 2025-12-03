# Case Study - Migrating Gradle-Client

## Introduction

In 2025 we migrated the [`gradle client`](https://github.com/gradle/gradle-client) application to use Declarative Gradle build scripts.

This migration included defining several custom Project Features to represent the plugins and tools used by one of its projects, and replacing all Kotlin buildscripts with DCL files.
Doing this gave us the opportunity to explore the DCL migration process in depth and identify common stumbling blocks and areas for improvement.

This document captures our experiences and presents them as a case study.
It highlights pain points, gotchas, and other insights surfaced by an actual migration.
You can view the results of the migration by comparing [the initial state of the repository](https://github.com/gradle/gradle-client/tree/8d5c4fefb10d7feae402fcae3106310a0495f535) with the [final post-migration state](https://github.com/gradle/gradle-client/tree/8a322e6e5f8878fe1228dea7e2e1e3683ef219f0).

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

For the migration of the Gradle Client project, and to support the latest Declarative Gradle Configuration Language (DCL), we often used nightly snapshot versions to access DCL features that were still under active development.
By the time you read this, a later milestone or release candidate of Gradle  may be available for use in your own migrations.  See the [Gradle releases page](https://gradle.org/releases/) to find the latest versions available.

The Declarative Gradle prototype includes a Kotlin Multiplatform (KMP) prototype plugin.
As the `:gradle-client` project is a Kotlin Multiplatform project (which only actually targets the JVM), we applied the corresponding Ecosystem Plugin in the root project's existing `settings.gradle.kts` file:

```
plugins {
    id("org.gradle.experimental.kmp-ecosystem").version("0.1.48")
}
```

Next, we renamed  `settings.gradle.kts` to `settings.gradle.dcl`.

Because the formats are similar for most common settings, the contents didn’t change significantly during the conversion.

You can compare the changes directly:

- [Resulting `settings.gradle.dcl` file](https://github.com/gradle/gradle-client/blob/856e92c3746be5c0ab2a3c8fac8e5b88066c0bcf/settings.gradle.dcl)
- [Original `settings.gradle.kts` file](https://github.com/gradle/gradle-client/blob/8d5c4fefb10d7feae402fcae3106310a0495f535/settings.gradle.kts)

A few changes stand out in the converted settings file:

- There’s no need for `@file:Suppress("UnstableApiUsage")`, this is done automatically for Declarative files
- We’ve added an included build and applied the `org.gradle.client.ecosystem.custom-ecosystem` plugin (more on these changes below)
- Content filtering of repositories isn’t available in DCL yet 
(this is likely to change in a future release, but as it is “merely” a performance optimization here, it can be omitted without impacting build correctness)
- `repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS` becomes `repositoriesMode = FAIL_ON_PROJECT_REPOS`, as DCL automatically understands enum types used in assignments
- The imperative check for Java 17 is removed
Declarative projects typically specify the JDK version they require as part of the project definition directly inside their Project Type, so this is no longer necessary

## Migrating Incrementally

Attempting to convert an entire build to DCL in one step is rarely practical except for the simplest builds.
Fortunately, migrations can be done incrementally, both across the build and within individual projects. 
Just like you can mix Groovy and Kotlin DSLs in a multi-project build, you can also mix declarative and non-declarative build scripts.
Inside each individual project’s buildscript, you can incrementally introduce declarative Project Types alongside imperative configuration logic.

To get started, we set the `org.gradle.kotlin.dsl.dcl=true` flag in the root project's `gradle.properties` file.
We could then add a Project Type to an existing build file and move configuration code into that Project Type piece-by-piece while deleting the corresponding code from the remaining “imperative” part of our build script outside its declaration.

This workflow made it easy to confirm correctness along the way, such as ensuring dependencies were still available for compilation after moving them into the Project Type.

This approach is also helpful because there is always a clear visual boundary between what parts of the build have already been made declarative and what parts are still pending.

## Using Existing Prototype Plugins

Core functionality, such as typical `implementation`, `api`, and test dependencies for library projects in common ecosystems, is already supported by the existing Project Types provided by Gradle's prototype plugins.

Migrating project dependencies declared in the top-level `dependencies` block to the dependency support provided by Project Types served as a good test of our DCL setup.

In the `gradle-client` build, the `:build-action` project was a straightforward candidate for migration. 
As a plain Java library, it was fully compatible with the existing `javaLibrary` prototype Project Type without any modifications.
The resulting `build-action/build.gradle.dcl` is simple and expressive:

```
javaLibrary {
    javaVersion = 8

    dependencies {
        implementation("org.gradle:gradle-tooling-api:9.2.0-milestone-2")
        implementation("org.gradle:gradle-declarative-dsl-tooling-models:9.2.0-milestone-2")
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

The DCL version is more concise and communicates the same intended "project definition".
The main disadvantage is the use of hardcoded GAV (Group:Artifact:Version) strings for dependencies, rather than the type-safe references provided by the Version Catalog in the original. 
This is a temporary limitation, and support for shared version declarations is expected in a future EAP.

The migration of the `:mutations-demo` project is similarly straightforward.  
It uses Kotlin and was migrated using the `kotlinJvmLibrary` prototype Project Type:

The new `mutations-demo/build.gradle.dcl`:

```
kotlinJvmLibrary {
    dependencies {
        implementation("org.gradle:gradle-declarative-dsl-core:9.2.0-milestone-2")
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

As a result, we could not simply apply and configure the existing `kotlinApplication` Project Type without removing vital features from the project.

To address this, we set up an included build to define some new plugins that would provide the necessary project features for SQL Delight, Detekt, and Compose.
We also defined a new Ecosystem Plugin that exposes these Project Features so that we can use them for projects in our build.

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
    id("org.gradle.experimental.plugin-ecosystem") version("0.1.48")
}
```

This allows us to use the `javaGradlePlugin` plugin, which is necessary for _declaratively_ writing Gradle plugins—like the custom Project Features we’re about to create here.


Next, we created a `build-logic/plugins` subproject that contains our new Project Features.  
We included the project in the included build’s `settings.gradle.dcl` as usual.
In `build-logic/plugins`, we created a `build.gradle.dcl` file with an empty top-level `javaGradlePlugin {}` block. 
This is where we will register our new plugins.

While we could have registered the plugins directly in the root project of the `:build-logic` build, this extra level of separation could be useful if you have other imperative logic to migrate that wouldn’t be entirely contained in new Project Features.

This structure allows for:

- Creating other plugins, such as other settings plugins that provide additional task types or global configuration that you could then apply to your build.
- Keeping different types of logic isolated from the projects that produce your Project Feature plugins.

### Writing a New Project Feature Plugin

The project we’re migrating is a Compose Desktop application written in Kotlin.
To support it declaratively, we’ll define a custom Project Feature plugin that provides Compose functionality. 
In `build-logic/plugins/src/main/java`, we begin by adding a new plugin class: `org.gradle.client.projectfeatures.compose.ComposeProjectFeaturePlugin` that implements `Plugin<Project>` and has a `public void apply(Project)` - just like every Gradle project-based plugin.

The `build-logic/plugins/src/main/java/org/gradle/client/projectfeatures/compose/ComposeProjectFeaturePlugin.java` file begins like this:

```
package org.gradle.client.projectfeatures.compose;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public abstract class ComposeProjectFeaturePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {}
}
```

The name of the Project Feature class itself does not need to be concise since it is not visible to consumer builds.
What the consumer will see is the name of the declarative definition exposed by the plugin. 
For clarity and usability, we chose a short but descriptive definition name: `compose`.

To support configuring our Project Feature, we need to define a definition interface that represents the DSL that consumers will configure declaratively, and a build model interface that represents the low-level details of what configuration the feature exposes to build logic.

In `build-logic/plugins/src/main/java/org/gradle/client/projectfeatures/compose/Compose.java` we add an empty interface that extends the `Definition` interface:

```
package org.gradle.client.projectfeatures.compose;

import org.gradle.api.internal.plugins.Definition;

public interface Compose extends Definition<ComposeBuildModel>{}
```

In `build-logic/plugins/src/main/java/org/gradle/client/projectfeatures/compose/ComposeBuildModel.java` we add an empty interface that extends the `BuildModel` interface:

```
package org.gradle.client.projectfeatures.compose;

import org.gradle.api.internal.plugins.BuildModel;

public interface ComposeBuildModel extends BuildModel {}
```

That’s all we need to get started.

Next we need to connect the plugin class to the feature.
We do this by annotating the plugin class with `@BindsProjectFeature` specifying a `ProjectFeatureBinding` class that connects the definition and build model types and registers the feature name that will be used in DCL files.

This gives us the following `build-logic/plugins/src/main/java/org/gradle/client/projectfeatures/compose/ComposeProjectFeaturePlugin.java`:

```
package org.gradle.client.projectfeatures.compose;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.BindsProjectFeature;

@SuppressWarnings("UnstableApiUsage")
@BindsProjectFeature(ComposeProjectFeaturePlugin.Binding.class)
public abstract class CustomDesktopComposeApplicationPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {}
    
    static class Binding implements ProjectFeatureBinding {
        @Override
        public void bind(ProjectFeatureBindingBuilder builder) {
            builder.bindProjectFeatureToBuildModel("compose", Compose.class, KotlinMultiplatformBuildModel.class,
                (context, definition, buildModel, parent) -> {
                }
            );
        }
    }
}
```

Notice that the call to the binding method provides all the necessary information to connect our feature to the declarative DSL.
First, we specify the name of the feature as it will appear in DCL files: `compose`.
Second, we specify the definition type of our feature: `Compose.class`.
Third, we specify the target type that the feature can be bound to: `KotlinMultiplatformBuildModel.class` (since our feature is intended to be used with KMP projects).
Finally, we provide a lambda that will be called to configure the build model based on the definition.

We specified the target type as `KotlinMultiplatformBuildModel.class` because we want this feature to be available for KMP projects.
We are binding to a _build model_ type, rather than a _definition_ type.
We _could_ bind to a definition type if we wanted to be very specific, but we should be interacting with our target through its build model, so as long as the build model conforms to our expectations, we don't actually care what the target definition is.
So instead, we are saying that this feature will work with _any_ definition that has a `KotlinMultiplatformBuildModel` build model type (or subtype).
This means that our feature can be used with a `KmpApplication` definition, a `KmpLibrary` definition, or any other KMP-based definition that has a `KotlinMultiplatformBuildModel` build model.

We add `@SuppressWarnings("UnstableApiUsage")` to the plugin class to silence distracting IDE warnings related to the use of in-development Declarative API types.
It’s a good idea to add this annotation to most of the Java classes in your plugin so that other actionable warnings are more visible.

Note that our definition is not a concrete class, nor is our build model.
This is expected when writing a Declarative Project Feature plugin - it should rely on Gradle to instantiate most objects automatically.

At this point, we’ve completed the minimum setup needed to define a (currently empty) Project Feature. 
All that remains is to package the plugin for use.

### Writing a New Ecosystem Plugin

In Declarative Gradle, you don’t apply plugins in project `build.gradle.dcl` files.
Why?
Because plugin application is imperative.

Telling Gradle to _apply_ a plugin is _build logic_ dealing with _how_ a build behaves, instead of defining _what_ a project represents.
Plugins contain imperative logic and are applied imperatively, so they do not belong in a project’s DCL file.

You can read more about Declarative Gradle design principles in our [blog post](https://blog.gradle.org/declarative-gradle-first-eap#developer-first-configuration).

To make a Project Feature available to a project, you apply an Ecosystem Plugin.
An Ecosystem Plugin:

- Implements `Plugin<Settings>`
- Is applied prior to project configuration
- Tells Gradle about Project Types and/or Features that will be used by projects in a build

In our case, we create our Ecosystem Plugin in  `build-logic/plugins/src/main/java/org/gradle/client/ecosystem/CustomEcosystemPlugin.java`:

```
package org.gradle.client.ecosystem;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.plugins.software.RegistersProjectFeatures;
import org.gradle.client.softwaretype.CustomDesktopComposeApplicationPlugin;


@RegistersProjectFeatures({ComposeProjectFeaturePlugin.class})
public abstract class CustomEcosystemPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings settings) {}
}
```

Although this is a complete implementation of a plugin, everything important is accomplished via the `@RegistersProjectFeatures` annotation so the `apply(Settings)` method is left empty.
This is typical for Ecosystem Plugins, and in the future, we may streamline this boilerplate even further.

Next, we register the Ecosystem Plugin in `build-logic/plugins/build.gradle.dcl` by adding an entry to the `registers` block:
 
```
javaGradlePlugin {
    description = "Declarative plugins containing custom project features for the gradle-client project."

    registers {
        id("org.gradle.client.ecosystem.custom-ecosystem") {
            description = "A custom ecosystem plugin registering the Project Feature plugins provided by this project"
            implementationClass = "org.gradle.client.ecosystem.CustomEcosystemPlugin"
        }
    }
}
```

That’s actually all that’s needed for our Ecosystem Plugin — its sole purpose is to register Project Feature plugins so they can be used declaratively in the build.

It’s important to understand that if our vision for Declarative Gradle comes to fruition, then writing your own Ecosystem Plugins won’t be something you’ll need to do for every project.
Our long-term goal is to offer a large assortment of Ecosystem Plugins available for popular frameworks and project types.
Combined with our work on Composability and Extensibility, migrating to a Declarative build should feel more like ordering a multi-course meal from a curated menu than cooking everything from scratch yourself.

Before going any further, now is a good time to sanity check our setup by applying our new Project Type to the `:gradle-client` project.
Even though the plugin currently does absolutely nothing, that’s expected and is not a problem.
A Kotlin DSL buildscript can use Project Types alongside imperative logic – so for now, the imperative logic will handle 100% of the work. 
We’re simply putting the Project Type in place to verify the relevant wiring is correct before continuing.

In the root `settings.gradle.dcl` file, we apply the Ecosystem Plugin to the `plugins` block:

```
plugins {
    // ... other existing plugin declarations
    id("org.gradle.client.ecosystem.custom-ecosystem")
}
```

In the `gradle-client/build.gradle.kts` file, we add to following line to the bottom of the file:

```
kotlinApplication {
    compose {
        // No configuration yet
    }
}
```

At this point, we should be able to build the project as usual.

While our Project Feature isn’t doing anything yet, a successful build confirms that Gradle has properly detected and configured it.

### Beginning to Migrate The Project’s Configuration: Dependencies

We started our migration with dependencies. 
This is often the best place to begin — dependencies are foundational to any build, and it's relatively easy to verify their correctness.

To migrate dependencies to the new DSL, we need a location within the DSL where they can be declared.
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

You can see this implemented in [the Kotlin prototype plugin](https://github.com/gradle/declarative-gradle/tree/v0.1.41/unified-prototype/unified-plugin/plugin-kmp/src/main/java/org/gradle/api/experimental/kmp/KmpLibrary.java), where the Project Type extends the `HasLibraryDependencies` interface defined in [HasLibraryDependencies.java]( https://github.com/gradle/declarative-gradle/tree/v0.1.41/unified-prototype/unified-plugin/plugin-common/src/main/java/org/gradle/api/experimental/common/HasLibraryDependencies.java):

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

When Gradle processes a DCL (or Kotlin DSL) file and finds Project Types it looks within their extension classes for any `@Nested` types that implement `Dependencies`, and records the `DependencyCollector` getter methods.
This allows method calls like `implementation(<some coordinates string>)` or `api(project(<some project>))` to map correctly to overloads of the `DependencyCollector.add(...)` present on the collectors returned by those getters.
It may look like magic, but it’s well-supported, and tools like IDEs understand these conventions through the Gradle Tooling API.

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
Swapping out the declarations in `jvmMain.dependencies` for the `jvm.dependencies` block yields the same result, as the `kotlinApplication` Project Type handles wiring dependencies into the correct configurations.

The JDK version declaration is also required by this Project Type, so it is required. 
Setting it to 17 here removes the need for the imperative version check that previously livesd in the settings file. 

With this minimal setup, the prototype plugin now takes care of wiring these dependencies into the appropriate configurations, and the project continues to build and run as before.

This is exactly the kind of reuse we’re aiming for—not just dependency declarations, but for other logic already built into the prototype plugin, like the JDK version declaration. 

### Wiring Additional Functionality from Plugins

This still leaves a significant portion of our application’s functionality unaccounted for — specifically, the “custom” parts: Compose, SQLDelight, and Detekt.

To address this, we gradually built support for each with a Project Feature. 
The goal is to migrate all functionality from the imperative parts of the build script into this unified declarative model until nothing imperative remains.
At this stage, there are multiple ways to approach the problem, and which one works best will depend on your specific needs and constraints.

Let’s continue our implementation of the Compose feature.

#### Supporting Compose

To support Compose, we used multiple levels of nesting to build a DSL that simplifies some of the Compose plugin’s extensions but is, in some ways, limited by our DCL's current feature set.

Designing Compose support involves the following:

1. Build new DSL definition objects to hold the configuration data needed by the plugin
2. Move the configuration from the imperative part of our buildscript into the new blocks using the definition types
3. Write code to be called by our plugin during application to wire values from our definition into the (Compose) plugin’s extension in an `afterEvaluate` closure

You can see the original Compose DSL configuration in the imperative buildscript [before migration](https://github.com/gradle/gradle-client/blob/b498fd896d858aecf4403ab077a4ea14bcd484d0/gradle-client/build.gradle.kts#L97-L147) and the resulting DCL [after migration](https://github.com/gradle/gradle-client/blob/2b52c756913e201e283eed9ccbed9ce95e53004e/gradle-client/build.gradle.dcl#L87-L155).
Rather than walk through the full implementation line-by-line (which closely mirrors the structure of the Detekt and SQLDelight support), we’ll highlight a few notable aspects of our Compose support.

##### Representing JVM arguments with `NDOC`s

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

When you look at the [Compose definition](https://github.com/gradle/gradle-client/blob/119811ff8b44dfb7b812fc8428dd30d5f680959b/build-logic/plugins/src/main/java/org/gradle/client/projectfeatures/compose/Compose.java), you’ll see that this block is modeled using an `NDOC`.
That might seem like a strange choice, given that not all JVM arguments manifest in a similar manner.
For instance, some JVM arguments are flags that don’t take a value, while others are a name-value pair that are concatenated into a single argument on the command line, and yet other arguments are name-value pairs that are passed as two separate command line arguments.
In other words, a collection of JVM arguments would be better represented as a more nuanced data structure.
In fact, arguments for a JVM are a cross-cutting concern that could be expressed by a common building-block type and reused in many different places in the DSL.

So if you’re looking at this bit of code and thinking it’s both more complex and more limited than it should be — don’t worry, we feel the same.
In future versions of Gradle, we expect to provide better support for concepts like polymorphic domain object containers and common building blocks that can be reused across different Project Types and/or Features.

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
You can see this in our feature [on this line](https://github.com/gradle/gradle-client/blob/119811ff8b44dfb7b812fc8428dd30d5f680959b/build-logic/plugins/src/main/java/org/gradle/client/projectfeatures/compose/ComposeProjectFeaturePlugin.java#L68) where we call the parameter-less `buildTypes.getRelease()` method directly.

##### Defining a Build Model

In order to implement our compose feature, we need to define a build model for it.
The build model represents how we want to deal with the low-level details of the compose plugin internally, and how we want other build logic to potentially interact with our feature.
In many ways, it represents the "API" of our feature.

However, unlike a completely new feature, we’re building a feature on top of an existing 3rd party plugin.
This means our feature mostly exists to configure the existing plugin, and implements little to no build logic itself.
Our interface to the legacy plugin is the extension it provides, so the `ComposeExtension` object is essentially our build model.

```
package org.gradle.client.projectfeatures.compose;

import org.gradle.api.internal.plugins.BuildModel;
import org.jetbrains.compose.ComposeExtension;

public interface ComposeBuildModel extends BuildModel {
    ComposeExtension getComposeExtension();
}
```

We also need to be able to set the extension in our build model, but we don't want to expose the ability to set the extension to other build logic.
To achieve this, we provide an implementation type for our build model that exposes a setter for the extension, but we keep the interface clean by only exposing the getter to other build logic.

```
package org.gradle.client.projectfeatures.compose;

import org.jetbrains.compose.ComposeExtension;

public class DefaultComposeBuildModel implements ComposeBuildModel {
    private ComposeExtension composeExtension;

    @Override
    public ComposeExtension getComposeExtension() {
        return composeExtension;
    }

    public void setComposeExtension(ComposeExtension composeExtension) {
        this.composeExtension = composeExtension;
    }
}
```

Finally, we need to tell Gradle to use our implementation type when instantiating the build model.
We do this by setting the implementation class in the binding:

```
static class Binding implements ProjectFeatureBinding {
    @Override
    public void bind(ProjectFeatureBindingBuilder builder) {
        builder.bindProjectFeatureToBuildModel("compose", Compose.class, KotlinMultiplatformBuildModel.class,
                (context, definition, buildModel, parent) -> {
                    ...
                }
        ).withBuildModelImplementationType(DefaultComposeBuildModel.class);
    }
```

We can now query and set the `ComposeExtension` instance from the third party plugin when applying the feature.  
However, this seems like a lot of ceremony in order to wire an existing extension into our build model, doesn't it?
In future versions of Gradle, we expect to provide simpler ways to wrap existing plugin extensions into build models.
Ideally, this would be as simple as declaring that a specific property in the build model represents a project extension and then there would be no need for a separate implementation type - Gradle would simply handle the wiring automatically when instantiating the build model.

#### Supporting Detekt

When supporting a third-party plugin like Detekt, there are typically two steps involved:

1. We need to create a new nested block in our Project Type to hold configuration data for that plugin
2. We need to apply the plugin wiring that configuration data as needed

The existing block used to configure Detekt in the imperative buildscript is quite simple:

```
detekt {
    source.setFrom("src/jvmMain/kotlin", "src/jvmTest/kotlin")
    config.setFrom(rootDir.resolve("gradle/detekt/detekt.conf"))
    parallel = true
}
```

We [translated](https://github.com/gradle/gradle-client/blob/119811ff8b44dfb7b812fc8428dd30d5f680959b/build-logic/plugins/src/main/java/org/gradle/client/projectfeatures/detekt/Detekt.java) this into a simple definition type that uses `Property` types compatible with DCL:


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

We’re not _recreating_ the exact types from the Detekt Gradle plugin, but modeling the inputs we need in a way that makes sense for our project and is compatible with the DCL.
That’s why we use lists of files and directories rather than `ConfigurableFileCollection` for the source and configuration files.
We fully utlize Gradle’s Lazy Provider APIs and wrap each data type in the appropriate type of `Property`.

If you compare the before and after commits for the `:gradle-client` project’s build script, you can see how the Detekt configuration was migrated from the original imperative style to the new declarative form nested under the `detekt` feature definition.

```
detekt {
    source = listOf(layout.projectDirectory.dir("src/jvmMain/kotlin"), layout.projectDirectory.dir("src/jvmTest/kotlin"))
    config = listOf(layout.settingsDirectory.file("gradle/detekt/detekt.conf"))
    parallel = true
}
```

In general, creating new definitions like this is straightforward. 
You can reference the existing DSL to see which values are being set, then define matching `Property` getters and configure methods with the appropriate DCL annotations.

At this point, though, builds don’t actually run Detekt yet. We’ve only modeled the configuration — we still need to wire the configured definition to the existing plugin extension.
We do this in the apply action we provide when binding the Detekt feature.

Here is our feature binding for Detekt, implemented in `DetektProjectFeaturePlugin.java`:

```
static class Binding implements ProjectFeatureBinding {
    @Override
    public void bind(ProjectFeatureBindingBuilder builder) {
        builder.bindProjectFeatureToBuildModel("detekt", Detekt.class, KotlinMultiplatformBuildModel.class,
                (context, definition, buildModel, parent) -> {

                    Project project = context.getProject();
                    project.getPluginManager().apply("io.gitlab.arturbosch.detekt");
                    ((DefaultDetektBuildModel)buildModel).setDetektExtension(project.getExtensions().findByType(DetektExtension.class));

                    buildModel.getDetektExtension().getSource().from(definition.getSource());
                    buildModel.getDetektExtension().getConfig().from(definition.getConfig());

                    project.afterEvaluate(p -> {
                        // This is not a property, need to wire this in afterEvaluate
                        buildModel.getDetektExtension().setParallel(definition.getParallel().get());
                    });
                }
        ).withBuildModelImplementationType(DefaultDetektBuildModel.class);
    }
}
```

We apply the Detekt plugin programmatically, which means it must be available on the classpath when our Project Type is applied.
To make that possible, we add it as an `api` dependency in our plugin’s [buildscript](https://github.com/gradle/gradle-client/blob/119811ff8b44dfb7b812fc8428dd30d5f680959b/build-logic/plugins/build.gradle.dcl):

```
javaGradlePlugin {
    dependencies {
        // ... other deps
        api("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.6")
    }
    // ... other configuration
}
```

With this in place, applying our custom `detekt` Project Feature gives the project full Detekt support, with the configuration fully migrated to the declarative model.

##### The Need for `afterEvaluate`

One key detail in our wiring method is the use of `Project.afterEvaluate` to wrap the wiring of the data in our new extension (`Detekt`) to the Detekt plugin’s extension (`DetektExtenstion`).

If you look at the `setParallel` method on the `detekt` instance (the Detekt plugin’s extension) and you’ll notice it only accepts a boolean value.
There is no overload that accepts a `Property<Boolean>`, which is what we’ve used to model this value in our DCL extension.
To work around this, we need to retrieve the value of our lazy `Property` via `get()` and call `setParallel()` using the result - but this can only be done once the model has been populated with data configured in the DCL.

Since the DCL runtime applies the plugin that configures the Project Type first and only then fills the model with the data from the `build.gradle.dcl` file, the plugin application code (starting from the `apply` method) cannot rely on the full model content being present at that time. 
The code configuring the plugin will not be able to eagerly access the final values in the model’s Java bean properties and plain collections at that point and will have to wait until later for them to be present.
Using `afterEvaluate` delays our access to this data until Gradle is ready to provide them.
This is a necessary workaround at the moment and a common pattern when building Declarative Project Types that use 3rd party plugins.

Because we need to delay setting the parallel value, we wrapped all of the Detekt configuration in `afterEvaluate` (even the source and config `FileCollection` properties, which are amenable to lazy wiring - note there is no call to `get()` involved in the DSL types here).
When reading this code, keep in mind that as the comment explains, this is only _necessary_ when a plugin does not make use of Gradle’s Lazy Provider API.

With this logic in place, we can now simply reference the `detekt` block in our DCL file, and the Detekt plugin will be applied and configured.

```
kotlinApplication {
    detekt {
        source = listOf(layout.projectDirectory.dir("src/jvmMain/kotlin"), layout.projectDirectory.dir("src/jvmTest/kotlin"))
        config = listOf(layout.settingsDirectory.file("gradle/detekt/detekt.conf"))
        parallel = true
    }
}
```

#### Supporting SQLDelight

Supporting SQLDelight follows the same process we used for Detekt, with one key difference: the SQLDelight plugin models its configuration using a `NamedDomainObjectContainer` (NDOC).

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

To replicate this structure in our DCL-based feature definition, we model the databases block using a `NDOC`.
This allows us to preserve much of the original DSL structure, now providing a `sqlDelight` block that can be added to a `kotlinApplication` definition:

```
kotlinApplication {
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
}
```

Note that by default, an `NDOC` named `databases` will assume the method used to create a new element is called `database`. 
This can be customized, but the default behavior is sufficient for our needs.
We kept the name `database` instead of using `create`, because it sounds more declarative and aligns better with the style we’re aiming for than a method called `create`, which has imperative connotations.

The DCL definition for the sqlDelight block is simple:

```
public interface SqlDelight {
    NamedDomainObjectContainer<Database> getDatabases();
}
```

Notice that the `NDOC` doesn’t require any special annotation - Declarative Gradle understands how `NDOC`s work and handles them automatically.

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

The wiring is handled in the `SqlDelightProjectFeaturePlugin.Binding` class:

```
static class Binding implements ProjectFeatureBinding {
    @Override
    public void bind(ProjectFeatureBindingBuilder builder) {
        builder.bindProjectFeatureToBuildModel("sqlDelight", SqlDelight.class, KotlinMultiplatformBuildModel.class,
            (context, definition, buildModel, parent) -> {
                Project project = context.getProject();

                definition.getVersion().convention("2.0.2");

                KotlinMultiplatformBuildModel parentBuildModel = context.getBuildModel(parent);
                parentBuildModel.getKotlinMultiplatformExtension().jvm(jvmTarget -> {
                    NamedDomainObjectProvider<DependencyScopeConfiguration> sqlDelightConfiguration = project.getConfigurations().dependencyScope("sqlDelightTool", conf -> {
                        stream(SQLDELIGHT_DEPENDENCY_MODULES).forEach(module ->
                            conf.getDependencies().addLater(definition.getVersion().map(version -> project.getDependencyFactory().create(SQLDELIGHT_GROUP + ":" +module + ":" + version)))
                        );
                    });

                    project.getConfigurations().named(jvmTarget.getCompilations().getByName("main").getDefaultSourceSet().getImplementationConfigurationName(), conf ->
                            conf.extendsFrom(sqlDelightConfiguration.get())
                    );
                });

                project.afterEvaluate(p -> {
                    project.getPluginManager().apply("app.cash.sqldelight");
                    ((DefaultSqlDelightBuildModel)buildModel).setSqlDelightExtension(project.getExtensions().getByType(SqlDelightExtension.class));

                    definition.getDatabases().forEach(featureDatabase -> {
                        buildModel.getSqlDelightExtension().getDatabases().create(featureDatabase.getName(), database -> {
                            database.getPackageName().set(featureDatabase.getPackageName());
                            database.getVerifyDefinitions().set(featureDatabase.getVerifyDefinitions());
                            database.getVerifyMigrations().set(featureDatabase.getVerifyMigrations());
                            database.getDeriveSchemaFromMigrations().set(featureDatabase.getDeriveSchemaFromMigrations());
                            database.getGenerateAsync().set(featureDatabase.getGenerateAsync());
                        });
                    });
                });
            }
        ).withBuildModelImplementationType(DefaultSqlDelightBuildModel.class);
    }
}
```

We thought: Why add an extra `project.afterEvaluate` callback unless we know we’re actually making use of SQLDelight by adding a database to the container in our extension?
Because the contents of the `NDOC` are only available _after_ project evaluation, this won’t work.
Assuming they will be ready earlier is an easy mistake to make when using an `NDOC`.

Other than this wrinkle, the rest of the wiring code should look familiar.
Since the SQLDelight plugin uses lazy Properties, we call `set(Provider)` and supply our extension's properties _without_ calling `get()` to realize their values.
This is how we envision most wiring working when the Provider API is present on both the plugin we are configuring and the declarative extension holding the values we are configuring it from.

In addition to applying the plugin and configuring its extension, we add the runtime dependencies needed by SQLDelight to the implementation configuration of the project’s JVM target.

### Finish making the build declarative

With support for Detekt, SQLDelight, and Compose fully modeled and represented as Project Features, we’re able to migrate all remaining configuration from the imperative parts of our buildscripts into the declarative `kotlinApplication` block.

At this point, we can rename the buildscripts from `build.gradle.kts` to `build.gradle.dcl` and run the build again to verify that everything has been properly wired.

If this build succeeds, the declarative migration is complete. 

We can move on to end-to-end verification of our project—running the project and confirming that all functionality still works as expected. 

## Limitations and Unmigrated Functionality

The biggest compromise we currently face when supporting features from 3rd party plugins is the reliance on `afterEvaluate`.
Ideally, plugin authors would use Gradle’s lazy Provider API for configuration. That would allow us to connect the lazy properties in our declarative extensions directly to the plugin’s own extension properties immediately when we apply a plugin.
Unfortunately, most plugins don’t yet support this, so we’re often forced to wire things up in `afterEvaluate` once all the values set in buildscripts are available programmatically. 
We hope this situation improves in the future, and we’re actively exploring more elegant solutions through our ongoing Composability and Extensibility work.

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
It’s satisfying to add a new property to your Project Type, hit refresh, and immediately get type-safe support and IDE feedback. 
You can test the wiring with a single build; and when it works, you know it’s wired correctly.

Future Declarative Gradle EAPs will continue improving this process. 
We’re actively working on filling in missing features and delivering a powerful new model for Composability and Extensibility. 

Stay tuned!
