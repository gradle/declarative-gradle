pluginManagement {
    includeBuild("../../workspace-settings")
}
plugins {
    id("org.gradle.experimental.settings.workspace")
}

configure<org.gradle.experimental.settings.WorkspaceSettings> {

/// NEW DSL

build {
    name = "platforms-subprojects"
}

layout {
    from("platforms/jvm")
    from("platforms/ide")
}

///
}