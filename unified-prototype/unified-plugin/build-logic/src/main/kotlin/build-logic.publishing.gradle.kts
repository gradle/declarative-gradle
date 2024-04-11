import java.util.regex.Matcher
import java.util.regex.Pattern

plugins {
    id("com.gradle.plugin-publish")
    id("com.github.johnrengelman.shadow") apply false
    signing
}

val commonOnlyJarDependencyScope = configurations.dependencyScope("commonOnlyJarDependencyScope")
val commonOnlyJar = configurations.resolvable("commonOnlyJar") {
    extendsFrom(commonOnlyJarDependencyScope.get())
}

dependencies {
    commonOnlyJarDependencyScope(project(":plugin-common")) {
        isTransitive = false
    }
}

tasks.shadowJar {
    configurations = listOf(commonOnlyJar.get())
    archiveClassifier.set("")
    relocate(
        "org.gradle.api.experimental.common",
        Matcher.quoteReplacement("org.gradle.api.experimental.common.0shaded.into.${project.name}"),
    )
}

gradlePlugin {
    website = "https://github.com/gradle/declarative-gradle"
    vcsUrl = "https://github.com/gradle/declarative-gradle"
}

signing {
    useInMemoryPgpKeys(
        project.providers.environmentVariable("PGP_SIGNING_KEY").orNull,
        project.providers.environmentVariable("PGP_SIGNING_KEY_PASSPHRASE").orNull
    )
}

gradle.taskGraph.whenReady {
    signing.isRequired = allTasks.stream().anyMatch { it is com.gradle.publish.PublishTask }
}
