pluginManagement {
    includeBuild("../../../workspace-settings")
}
plugins {
    id("org.gradle.experimental.settings.workspace")
}

configure<org.gradle.experimental.settings.WorkspaceSettings> {

/// NEW DSL
build {
    name = "explicit-only"
}

layout {
    subproject("core-1", "core/core-1")
    subproject("core-2", "core/core-2")
    subproject("integ-test-1", "tests/integ-test/integ-test-1")
    subproject("integ-test-2", "tests/integ-test/integ-test-2")
    subproject("perf-test-1", "tests/perf-test/perf-test-1")
    subproject("perf-test-2", "tests/perf-test/perf-test-2")
    subproject("utils-1", "utils/utils-1")
    subproject("utils-2", "utils/utils-2")
}

////
}
