plugins {
    id("com.gradle.plugin-publish")
    signing
}

gradlePlugin {
    website = "https://blog.gradle.org/declarative-gradle"
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

// we want plugins to preserve method parameter names in class files, useful in IDE auto-completion
tasks.compileJava {
    options.compilerArgs.add("-parameters")
}