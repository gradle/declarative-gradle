# Declarative Gradle

[![a](https://img.shields.io/badge/slack-%23declarative_gradle-brightgreen?style=flat&logo=slack)](https://gradle.org/slack-invite)
[![a](https://img.shields.io/badge/Getting-Started-blue?style=flat)](./docs/getting-started/README.md)
[![a](https://img.shields.io/badge/Roadmap-Public-brightgreen?style=flat)](./ROADMAP.md)

At Gradle, part of our vision is to provide an elegant and extensible declarative build language
that enables expressing any build in a clear and understandable way.
We are working on _Declarative Gradle_ to realize that part of the vision.
This is an experimental project, stay tuned for updates!

Learn more in the [Declarative Gradle Announcement](https://blog.gradle.org/declarative-gradle)
blog post.

## Disclaimer

!!! info
    _Declarative Gradle_ is an experimental project.
    Currently, no compatibility is guaranteed, and there is no commitment to the DSL syntax
    and available features.
    More information will be released soon.
    Any feedback is welcome!
    See more on the [Contributor Guide](./docs/CONTRIBUTING.md).

## Key Principles

- **Ease of use** for regular software developers.
  Software developers should be able to define any software and build their projects
  without the need to understand the details of how the build system works.
- **Complete flexibility** for build engineers and advanced users.
  Experienced Gradle users should maintain the current level of flexibility and be able
  to automate a wide range of software build automation scenarios with custom build logic.
- **Excellent IDE integration.**
  Importing the software project to the IDE and interacting with it should be fast and fully reliable.
  IDEs and other tools should be able to change the definition automatically or through UI reliably.

We implement those principles through a _declarative DSL_ which is, at the moment, based on Kotlin.
The [Declarative Gradle Announcement](https://blog.gradle.org/declarative-gradle)
outlines more details about the project and the new
Declarative DSL we are building.

<!-- TODO: Add project manifesto -->

## Concept

Here are a few very brief examples of what the Declarative Gradle syntax may look like.
As noted above, this syntax is experimental and might change during the experiment.

### Java Libraries

A typical Java library, which targets a single version of Java, might look like this:

```kotlin
javaLibrary {
    publishedAs("my-group:my-lib:2.0")

    dependencies {
        api("some:lib:1.2")
        implementation(projects.someLib)
    }

    // This library targets Java 21 only
    java(21)

    tests {
        unit {
            dependencies {
                implementation("some:other-lib:1.4")
            }
        }
    }
}
```

### Multi-target Projects

This example shows the definition of a Java library that targets both Java 11 and 21:

<details>
  <summary>Show Code</summary>

```kotlin
// Declare the type of software that the project produces
// There is no plugin application, as Gradle infers this from the "javaLibrary" type definition
javaLibrary {
    // All information about the library is grouped here

    // GroupID/ArtifactID/Version for publishing
    publishedAs("my-group:my-lib:2.0")

    // Common dependencies for all targets
    dependencies {
        api("some:lib:1.2")
        implementation(projects.someLib)
    }

    // A library might have more than one target
    targets {
        // All information about specific targets is grouped here
        
        // Declare Java 11 as a target
        java(11) {
            // Specific information about Java 11 target
            
            // An additional dependency that is used only for Java 11
            dependencies {
                implementation("some:back-port-lib:1.5")
            }
        }

        // Declare Java 21 as a target, with no additional information
        java(21)
    }
    
    tests {
        // All information about the tests is grouped here
        
        unit {
            // Dependencies for the unit tests
            dependencies {
                implementation("some:other-lib:1.4")
            }
        }
    }
}
```

</details>

## Get Started

See the [Getting Started Guides](./docs/getting-started/README.md).

## Prototypes

Here are the experimental prototypes
currently available for initial review and evaluation:

- [Declarative Gradle Prototype](./unified-prototype/README.md) - prototypes of plugins for JVM, Android, Kotlin and KMP projects built using "unified" plugins that all utilize a similar model and implemented using the Declarative DSL
- [Now In Android](https://github.com/gradle/nowinandroid/tree/main-declarative) -
  a port of a popular Android demo app that showcases the [Support for Android](./docs/android/README.md)
  in Declarative Gradle.
- [Other Early prototypes](./early-prototypes/README.md) -
Initial prototypes were created for feedback and discussion purposes.

## License

All text/documentation content is open source and licensed under the
[Creative Commons Attribution-NonCommercial-ShareAlike 4.0 License](./LICENSE.txt).
Some code samples may be licensed under the Apache License v2.0
or other permissive OSI-compliant licenses.

## Learn More

- [Initial Declarative Gradle Announcement and Full Manifesto](https://blog.gradle.org/declarative-gradle)

## Discuss

- `#declarative-gradle` channel on the [Gradle Community Slack](https://gradle.org/slack-invite)
- [Dedicated category](https://discuss.gradle.org/c/help-discuss/declarative-gradle/38) Gradle Forums

See [Gradle Community Resources](https://gradle.org/resources/) for the links to the channels.
