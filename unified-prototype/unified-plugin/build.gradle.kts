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

    // CI agents enable Maven settings.xml mirrors with this Gradle property in the Gradle user home, because
    // they block Maven Central. TestKit builds use their own Gradle user home, so tests pass it on explicitly.
    val mavenSettingsMirror = providers.gradleProperty("org.gradle.mirror.maven.settings")
    tasks.withType<Test>().configureEach {
        mavenSettingsMirror.orNull?.let { systemProperty("org.gradle.mirror.maven.settings", it) }
    }

    tasks.withType<Javadoc>().configureEach() {
        options {
            val standardOptions: StandardJavadocDocletOptions = this as StandardJavadocDocletOptions
            standardOptions.addStringOption("Xdoclint:none", "-quiet") // Suppress "warning: no comment" warnings
        }
    }
}
