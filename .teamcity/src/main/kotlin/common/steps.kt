package common

import jetbrains.buildServer.configs.kotlin.buildSteps.GradleBuildStep

private val JDK_INSTALLATIONS = listOf(
    "%linux.java21.openjdk.64bit%",
    "%linux.java17.openjdk.64bit%",
    "%linux.java11.openjdk.64bit%",
)

fun configuredGradle(init: GradleBuildStep.() -> Unit) = GradleBuildStep {
    useGradleWrapper = true
    buildFile = "" // Let Gradle detect the build script
    enableStacktrace = true
    gradleParams = "-Porg.gradle.java.installations.paths=${JDK_INSTALLATIONS.joinToString(",")}"

    init()
}

fun GradleBuildStep.addGradleParam(value: String) {
    gradleParams = when {
        gradleParams.isNullOrEmpty() -> value
        else -> "$gradleParams $value"
    }
}
