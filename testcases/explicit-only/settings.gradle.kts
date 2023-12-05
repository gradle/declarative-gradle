pluginManagement {
    includeBuild("../../workspace-settings")
}
plugins {
    id("org.gradle.experimental.settings.workspace")
}

configure<org.gradle.experimental.settings.WorkspaceSettings> {

/// NEW DSL
projects {
    name = "explicit-only"
    directory("core") {
        subproject("core-1")
        subproject("core-2")
    }
    directory("tests") {
        directory("integ-test") {
            subproject("integ-test-1")
            subproject("integ-test-2")
        }
        directory("perf-test") {
            subproject("perf-test-1")
            subproject("perf-test-2")
        }
    }
    directory("utils") {
        subproject("utils-1")
        subproject("utils-2")
    }
}

////
}