package common

import jetbrains.buildServer.configs.kotlin.buildSteps.GradleBuildStep

fun configuredGradle(init: GradleBuildStep.() -> Unit) = GradleBuildStep {
    useGradleWrapper = true
    buildFile = "" // Let Gradle detect the build script
    enableStacktrace = true

    init()
}
