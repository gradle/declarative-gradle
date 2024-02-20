# Unified Plugin Prototypes

This directory contains prototypes of plugins for JVM, Android and KMP projects built using "unified" plugins that all utilize a similar model and are implemented using the Declarative DSL.

Currently, these different ecosystems still apply distinct plugins, but those plugins all share a common `plugin-common` dependency, which will gradually grow to contain more functionality.

So far, the only example modified to use the Declarative DSL is the Android example.

## JVM

Not yet updated for Declarative DSL.

## KMP

Not yet updated for Declarative DSL.

## Android

In the `testbed-android` directory, run `../gradlew build` to build an `aar` for an example Android project build.
