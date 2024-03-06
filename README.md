# Declarative Gradle Prototypes

This repository contains prototypes and public examples
of the [Declarative Gradle](https://blog.gradle.org/declarative-gradle)
syntax and project definitions.

## Disclaimer

_Declarative Gradle_ is an experimental project.
At this point, no compatibility is guaranteed,
as well as there is no commitment to the DSL syntax
and available features.
More information will be released soon
Any feedback is welcome!

<!-- TODO: Add project manifesto -->

## Concept

Below a very brief example of how the Declarative Gradle syntax may look like. This example shows the definition of a Java library that targets both Java 11 and 21:

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

## Read More

- [Initial Declarative Gradle Announcement and Full Manifesto](https://blog.gradle.org/declarative-gradle)

## Discuss

- `#declarative-gradle` channel on the community Slack
- [dedicated category](https://discuss.gradle.org/c/help-discuss/declarative-gradle/38) Gradle Forums

See [Gradle Community Resources](https://gradle.org/resources/) for the links to the channels.

## License

All text/documentation content is open source and licensed under the
[Creative Commons Attribution-NonCommercial-ShareAlike 4.0 License](./LICENSE.txt).
Some code samples may be licensed under the Apache License v2.0,
or other permissive OSI-compliant licenses.
