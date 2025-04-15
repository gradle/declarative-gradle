# Declarative Gradle

[![a](https://img.shields.io/badge/slack-%23declarative_gradle-brightgreen?style=flat&logo=slack)](https://gradle.org/slack-invite)
[![a](https://img.shields.io/badge/Getting-Started-blue?style=flat)](./docs/getting-started/README.md)
[![a](https://img.shields.io/badge/Roadmap-Public-brightgreen?style=flat)](./docs/ROADMAP.md)

At Gradle, part of our vision is to provide an elegant and extensible declarative build language
that enables expressing any build in a clear and understandable way.
We are working on _Declarative Gradle_ to realize that part of the vision.
This is an experimental project, stay tuned for updates!

Learn more in the [Declarative Gradle Announcement](https://blog.gradle.org/declarative-gradle)
blog post and [other publications](./docs/publications/README.md).

## Status

_Declarative Gradle_ is an **experimental** project, now in its [third Early Access Preview (EAP)](./docs/EAP3.md).
We invite you to [try out Declarative Gradle](./docs/getting-started/README.md),
and to [share feedback](./docs/feedback.md).
The project includes the following experimental parts:

- Software Types as a declarative modeling framework
- Changes in Gradle to support DCL files
- Changes in Android Studio and IntelliJ IDEA to support DCL files
- A Visual Studio Code extension to support DCL files
- An Eclipse IDE plugin to support DCL files
- Prototype plugins demonstrating software types and higher-level models
- A Gradle test client demonstrating features not yet implemented in the IDE

Currently, no compatibility is guaranteed, and there is no commitment to the DSL syntax
and available features.
Learn how you can contribute in the [Participate](./docs/CONTRIBUTING.md) section.

<a class="button button--blue" href="./docs/feedback">
   Share Your Feedback
</a>

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

We implement those principles through a _declarative DSL_ which is a tailored tiny subset of the Kotlin language.
The [Declarative Gradle Announcement](https://blog.gradle.org/declarative-gradle)
outlines more details about the project and the new Declarative DSL we are building.

<!-- TODO: Add project manifesto -->

## A Glimpse

Here is a very brief example of what the Declarative Gradle syntax looks like for a Java application.

```kotlin
javaApplication {
    javaVersion = 21
    mainClass = "com.example.App"

    dependencies {
        implementation(project(":java-util"))
        implementation("com.google.guava:guava:32.1.3-jre")
    }
}
```

Looks familiar, right?

As noted above, this syntax is experimental and might change during the experiment.

## Learn More

The _Declarative Gradle_ experiment is still in an early stage but is ready for your feedback!

* [Getting Started](docs/getting-started/README.md) - Learn how to try _Declarative Gradle_ yourself.
* [Documentation](docs/reference/README.md) - Learn about the fundamentals behind _Declarative Gradle_.
* [Participate](docs/CONTRIBUTING.md) - Learn how you can help shape the future of _Declarative Gradle_.
* [Resources](docs/publications/README.md) - Learn more from conferences, interviews, articles, etc.

## Share Feedback

We invite you to [try out Declarative Gradle](./docs/getting-started/README.md),
and to [share your feedback](./docs/feedback.md).
