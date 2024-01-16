# Declarative Android-based projects

This explores ideas with a simpler DSL to configure applications and libraries written for Android.

All configuration is moved under a new `androidApplication` block.

## [Example](testbed/build.gradle.kts)

Gradle needs to run with Java 17.

Open the project in `android`.  The `testbed` subproject uses the new DSL.

The new DSL demonstrates several ideas:
- `dependencies {}` in the `androidApplication` block that are limited to only the scopes applicable to the application. 
- `targets {}` for configuring specific Android variants
- top-level properties backed by Providers (like `namespace`)
- `sources {}` for configuring the location of source files

