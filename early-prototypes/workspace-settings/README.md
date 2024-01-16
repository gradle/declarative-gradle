# Declarative project structure

This implements the ideas described in the [spec](https://docs.google.com/document/d/1mO7jZqIvFDLNtfJEMs9LgMgSI5_T56nBJIi60QtqUi8/edit?usp=sharing)

The spike implements new behavior and DSL as a settings plugin applied to some sample projects. 

Ideas that show promise will be copied into future prototypes or Gradle itself. 

## New ideas

### Auto-detection

In simple project layouts, Gradle should be able to auto-detect all projects that need to be included.

### Physical-location first 

The existing APIs require users to specify a logical path that is interpretted as a physical path by convention.

This DSL explores ideas to use the physical layout to include subprojects instead.

## Test cases

These are common layouts we found in real projects.

All of the test cases should be able to run on a recent version of Gradle. You can inspect the list of included projects by looking at `gradle projects`.

### [explicit-only](testcases/explicit-only/settings.gradle.kts)

In this example, all subprojects are explicitly included with a flat logical path and physical path.

### [logical-layout](testcases/logical-layout/settings.gradle.kts)

In this example, the logical path (`:myorg:app`) for the subproject is very different from its physical path.

### [platforms-subprojects](testcases/platforms-subprojects/settings.gradle.kts)

This example is close to Gradle's layout. Subprojects are automatically included from other directories than the root.

### [single-project](testcases/single-project/settings.gradle.kts)

This example only shows that the equivalent of `rootProject.name` is set outside the layout.

### [spring-boot-like](testcases/spring-boot-like/settings.gradle.kts)

This example follows a similar layout to Spring boot. There's a mix of subprojects that are found in subdirectories and under other projects.

### [subprojects-dir](testcases/subprojects-dir/settings.gradle.kts)

This example shows all subprojects being auto-discovered under a subdirectory (`subprojects/`).

### [top-level-projects](testcases/top-level-projects/settings.gradle.kts)

This example can auto-detect all subprojects with build files without any explicit configuration.