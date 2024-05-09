plugins {
    kotlin("jvm").version(libs.versions.kotlin).apply(false)
}

subprojects {
    group = "org.gradle.experimental"
    version = "0.1.0"
}

tasks.register("publishAllPlugins") {
    description = "Publish all plugins in the build"
    dependsOn(
        ":plugin-android:publishPlugins",
        ":plugin-jvm:publishPlugins",
        ":plugin-kmp:publishPlugins",
    )
}
