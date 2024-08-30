# Contributing to Declarative Gradle

[![a](https://img.shields.io/badge/slack-%23declarative_gradle-brightgreen?style=flat&logo=slack)](https://gradle.org/slack-invite)
[![a](https://img.shields.io/badge/Roadmap-Public-brightgreen?style=flat)](./ROADMAP.md)

_Declarative Gradle_ is an experimental project.
Currently, it is not ready for tinkering or wide adoption.
Discussion and sharing feedback is the recommended way of participation at the moment.

## Share Feedback

While the project is in early stages,
initial feedback on the DSL, early demos, features and use-cases will be appreciated!

- Fill out the [Feedback Form](./feedback.md)
- `#declarative-gradle` channel on the [Gradle Community Slack](https://gradle.org/slack-invite)

## Adopting Declarative Gradle in your projects

We do not consider Declarative Gradle is ready for wide adoption,
because there are upcoming breaking changes.

## Adapting Gradle Plugins

We advise against adapting your plugins at this stage
because we plan many breaking changes.

## Improving Documentation

At the moment, the documentation is implemented in Markdown as a part of this repository.
It is deployed as a Material for MkDocs subsite,
similar to other new community resources.
Later, it will be integrated into the main [Gradle Build Tool website](https://gradle.org/).

In particular, we want to improve the guidelines based on your experiences.
If you experiment with Declarative Gradle in your projects and wish to reference them,
it is more than welcome, too.

Any patches are welcome. Just submit a pull request!
To help with that, we implemented a Dev Container for the documentation
and a sub-site configuration for Declarative Gradle.
To have a live documentation sub-site:

1. Install the Dev Containers plugin in your IDE (VS Code or IntelliJ Platform)
2. Click on the _Re-open in the DEv Container_ button
3. Run `mkdocs serve`
