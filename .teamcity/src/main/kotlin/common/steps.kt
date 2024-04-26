package common

import jetbrains.buildServer.configs.kotlin.buildSteps.GradleBuildStep

fun configuredGradle(init: GradleBuildStep.() -> Unit) = GradleBuildStep {
    useGradleWrapper = true
    buildFile = "" // Let Gradle detect the build script
    enableStacktrace = true
    gradleParams = "-Porg.gradle.java.installations.paths=%linux.java21.openjdk.64bit%"

    param("env.ANDROID_HOME", "/opt/android/sdk")
    param("env.ANDROID_SDK_ROOT", "/opt/android/sdk")

    init()
}

fun GradleBuildStep.addGradleParam(value: String) {
    gradleParams = when {
        gradleParams.isNullOrEmpty() -> value
        else -> "$gradleParams $value"
    }
}
