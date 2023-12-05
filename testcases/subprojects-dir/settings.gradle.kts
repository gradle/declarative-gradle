pluginManagement {
    includeBuild("../../workspace-settings")
}
plugins {
    id("org.gradle.experimental.settings.workspace")
}

configure<org.gradle.experimental.settings.WorkspaceSettings> {

/// NEW DSL
build("subprojects-dir") {
    
}
projects {
    directory("subprojects") {
        autodetect = true
    }
}

///
}