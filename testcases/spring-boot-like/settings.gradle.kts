pluginManagement {
    includeBuild("../../workspace-settings")
}
plugins {
    id("org.gradle.experimental.settings.workspace")
}

configure<org.gradle.experimental.settings.WorkspaceSettings> {

/// NEW DSL

build {
    name = "spring-boot-like"

    directory("core") {
        autodetect = true
    }
    directory("tests") {
        subproject("integ-test") {
            // :integ-test:test-1
            // :integ-test:test-2
            autodetect = true
        }
        subproject("perf-test") {
            // :integ-test:test-1
            // :integ-test:test-2
            autodetect = true
        }
    }
    directory("utils") {
        autodetect = true
    }
    subproject("packaging")
}

///
}