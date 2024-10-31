plugins {
    id("base")
    kotlin("jvm").version(libs.versions.kotlin).apply(false)
    id("build-logic.build-update-utils")
}

version = file("version.txt").readText().trim()

subprojects {
    group = "org.gradle.experimental"
    version = parent!!.version
}

val rootCheck = tasks.named("check")

val publishAllPlugins = tasks.register("publishAllPlugins") {
    description = "Publish all plugins in the build"
}
subprojects {
    plugins.withId("build-logic.publishing") {
        publishAllPlugins.configure {
            dependsOn(tasks.named("publishPlugins"))
        }
    }
    rootCheck.configure {
        dependsOn(tasks.named("check"))
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

    tasks.withType<Javadoc>().configureEach() {
        options {
            val standardOptions: StandardJavadocDocletOptions = this as StandardJavadocDocletOptions
            standardOptions.addStringOption("Xdoclint:none", "-quiet") // Suppress "warning: no comment" warnings
        }
    }
}
