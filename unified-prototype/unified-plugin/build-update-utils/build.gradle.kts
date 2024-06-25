description = "Utility tasks for CI to use to release a version of the plugin"

val ciUpdateTasks = "CI Update Tasks"

val versionFile = rootProject.file("version.txt")

class ExecOperationsHelper @Inject constructor(val execOperations: ExecOperations) {
}

val execOperations = project.objects.newInstance<ExecOperationsHelper>().execOperations

tasks.register("changeSnapshotToReleaseVersion") {
    description = "Change the version of the plugin to a release version"
    group = ciUpdateTasks
    doLast {
        val version = getVersionFromFile()
        val newVersion = version.removeSuffix("-SNAPSHOT")
        if (version == newVersion) {
            throw IllegalStateException("Version is not a snapshot version: $version")
        }
        writeNewVersion(newVersion)
        execOperations.exec {
            commandLine("git", "commit", "-m", "Release version $newVersion", versionFile.absolutePath)
        }
        execOperations.exec {
            commandLine("git", "tag", "v${newVersion}", "-m", "Release version $newVersion")
        }
    }
}

tasks.register("changeReleaseToNextSnapshotVersion") {
    description = "Change the version of the plugin to a snapshot version"
    group = ciUpdateTasks
    doLast {
        val version = getVersionFromFile()
        if (!version.matches(Regex("\\d+\\.\\d+\\.\\d+"))) {
            throw IllegalStateException("Version is not a release version: $version")
        }
        val versionParts = version.split(".")
        val newVersion = "${versionParts[0]}.${versionParts[1]}.${versionParts[2].toInt() + 1}-SNAPSHOT"
        writeNewVersion(newVersion)
        execOperations.exec {
            commandLine(
                "git",
                "commit",
                "-m",
                "Switch to next snapshot version $newVersion",
                versionFile.absolutePath
            )
        }
    }
}

fun getVersionFromFile() = versionFile.readText().trim()

fun writeNewVersion(newVersion: String) = versionFile.writeText(newVersion)
