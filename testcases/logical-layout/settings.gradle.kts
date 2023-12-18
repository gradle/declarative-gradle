pluginManagement {
    includeBuild("../../workspace-settings")
}
plugins {
    id("org.gradle.experimental.settings.workspace")
}

configure<org.gradle.experimental.settings.WorkspaceSettings> {

/// NEW DSL

build {
    name = "logical-layout"
}

layout {
    autodetect = false
    subproject("myorg:app", "app")
}

///
}