pluginManagement {
    includeBuild("../../workspace-settings")
}
plugins {
    id("org.gradle.experimental.settings.workspace")
}

configure<org.gradle.experimental.settings.WorkspaceSettings> {

/// NEW DSL

projects {
    name = "subprojects-dir"
    directory("subprojects") {
        autodetect = true
    }
}

///
}