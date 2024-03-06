# Declarative Gradle

This repository contains prototypes and public examples
of the [Declarative Gradle](https://blog.gradle.org/declarative-gradle)
syntax and project definitions.

## Disclaimer

_Declarative Gradle_ is an experimental project.
Currently, no compatibility is guaranteed, and there is no commitment to the DSL syntax
and available features.
More information will be released soon.
Any feedback is welcome!

<!-- TODO: Add project manifesto -->

## Concept

Here are a few very brief examples of what the Declarative Gradle syntax may look like.
As noted above, this syntax is experimental and might change during the experiment.

### Java libraries

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

### Multi-target projects

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

## Prototypes

Here are the experimental prototypes
currently available for initial review.
They are not ready for evaluation at the moment:

- [Unified Prototype](./unified-prototype/README.md) - prototypes of plugins for JVM, Android, and KMP projects built using "unified" plugins that all utilize a similar model and implemented using the Declarative DSL
- [Other Early prototypes](./early-prototypes/README.md) -
Initial prototypes created for feedback and discussion purposes.

## License

All text/documentation content is open source and licensed under the
[Creative Commons Attribution-NonCommercial-ShareAlike 4.0 License](./LICENSE.txt).
Some code samples may be licensed under the Apache License v2.0,
or other permissive OSI-compliant licenses.

## Read More

- [Initial Declarative Gradle Announcement and Full Manifesto](https://blog.gradle.org/declarative-gradle)

## Discuss

- `#declarative-gradle` channel on the [Gradle Community Slack](https://gradle.org/slack-invite)
- [dedicated category](https://discuss.gradle.org/c/help-discuss/declarative-gradle/38) Gradle Forums

See [Gradle Community Resources](https://gradle.org/resources/) for the links to the channels.
