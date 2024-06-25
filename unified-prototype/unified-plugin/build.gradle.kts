plugins {
    kotlin("jvm").version(libs.versions.kotlin).apply(false)
}

version = file("version.txt").readText().trim()

subprojects {
    group = "org.gradle.experimental"
    version = parent!!.version
}

val publishAllPlugins = tasks.register("publishAllPlugins") {
    description = "Publish all plugins in the build"
}
subprojects {
    plugins.withId("build-logic.publishing") {
        publishAllPlugins.configure {
            dependsOn(tasks.named("publishPlugins"))
        }
    }
}

val publishAllPluginsToMavenLocal = tasks.register("publishAllPluginsToMavenLocal") {
    description = "Publish all plugins in the build to the Maven Local repository"
}
subprojects {
    plugins.withId("build-logic.publishing") {
        publishAllPluginsToMavenLocal.configure {
            dependsOn(tasks.named("publishToMavenLocal"))
        }
    }
}
