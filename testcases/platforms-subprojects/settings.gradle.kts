pluginManagement {
    includeBuild("../../workspace-settings")
}
plugins {
    id("org.gradle.experimental.settings.workspace")
}

configure<org.gradle.experimental.settings.WorkspaceSettings> {

/// NEW DSL

projects {
    name = "platforms-subprojects"
    directory("platforms") {
        subproject("jvm") {
            autodetect = true
        }
        subproject("ide") {
            autodetect = true
        }
    }
}

///
}