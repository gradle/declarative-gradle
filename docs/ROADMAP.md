# Declarative Gradle Roadmap

Declarative Gradle is an experimental project under active development.
Below, you can see the key milestones we anticipate.

!!!warn
    A few words of caution from our lawyers 😉
    > The information contained in this Declarative Gradle Roadmap (the "Roadmap") is intended to outline our general product direction, which is subject to change at any time. The content provided in this Roadmap is provided for informational purposes only and is not a commitment, promise or legal obligation to deliver any material, code, or functionality and should not be relied upon in making purchasing or other decisions. The development, release and timing of any features or functionality described in this Roadmap remains at the sole discretion of Gradle, Inc. Product capabilities, timeframes and features are subject to change and should not be viewed as commitments.

| Milestone                           |   | Date          |
| ----------------------------------- |:-:| ------------: |
| First Early Access Preview (EAP)    | ✅ |     July 2024 |
| Second Early Access Preview (EAP 2) | ✅ | November 2024 |
| Third Early Access Preview (EAP 3)  | 🏗️ | February 2024 |
| Incubating Software Types and DCL   | ⏳ |          2025 |
| Stable Software Types and DCL       | ⏳ |          2026 |

The Gradle feature lifecycle is documented in the [user manual](https://docs.gradle.org/current/userguide/feature_lifecycle.html#sec:incubating_state).

To discuss the roadmap and the related initiatives, use the
`#declarative-gradle` channel on the [Gradle Community Slack](https://gradle.org/slack-invite)

## First Early Access Preview (EAP)

* July 2024
* Early feedback from IDE vendors and plugin integrators
* Early feedback from software developers and build engineers from the community
* Demonstrating end to end workflows with [Now In Android](https://github.com/gradle/nowinandroid) sample and [other prototypes](https://github.com/gradle/declarative-gradle)
  * Declarative Configuration Language (DCL) preview
  * Software Types support preview
  * Android Studio DCL support preview
  * Toolability demonstrations (IDE integration, mutations)


## Second Early Access Preview (EAP)

* November 2024
* Official Android Software Type Preview
* New DCL Language Features
  * Enum properties
  * Named domain object containers
* Configuring Software Types from Kotlin DSL
* Prototype Plugins for C++ and Swift
* Support for VS Code and Eclipse IDE
* Generating Declarative Builds with `gradle init`

## Third Early Access Preview (EAP)

* February 2025
* Declarative Gradle can be used by early adopters for simple projects
  * Add support for testing to our prototype plugins
* Validating the Software Types approach for Composability and Extensibility
  * Supporting new software features that are not part of the base software type
  * Adding/configuring code quality checks
  * Adding/configuring source-generation/bytecode-manipulation
* More DCL features to support the official Android Software Type
  * File and directory properties
  * Multi-valued properties (Lists, Maps)
  * Container defaults
* Discovery work on the migration of existing builds

## Incubating Software Types and DCL

* 2025
* Software Types APIs are released as Incubating
  * Software Types can be configured from both Kotlin DSL and Groovy DSL
  * Core JVM Plugins expose Software Types
* DCL Language and Tooling APIs are released as Incubating
  * More language features to support key use cases (version catalogs, polymorphic containers etc.)
  * Built-in opinionated formatter/linter (cli & ide)
* Backwards compatibility concerns are addressed
  * Dealing with different versions of software types over time
  * Dealing with changes to DCL language over time
  * Dealing with changes to DCL tooling libraries over time
* IDE language support is enhanced
  * IntelliJ IDEA and Android Studio ship with DCL support
  * Language Server and associated IDE plugins are published
  * Better assistance by IDE (e.g. completion for values, refactorings)

## Stable Software Types and DCL

* 2026
* Software Types are promoted to Stable
* DCL Language and Tooling APIs are promoted to Stable
