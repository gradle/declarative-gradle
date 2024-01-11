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
}

layout {

    from("core")
    from("tests/integ-test")

    // :integ-test:test-1
    // :integ-test:test-2
    subproject("integ-test", "tests/integ-test")

    // :integ-test:test-1
    // :integ-test:test-2
    subproject("perf-test", "tests/perf-tests")

    from("utils")
}

///
}