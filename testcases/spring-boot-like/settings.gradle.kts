pluginManagement {
    includeBuild("../declarative-settings-plugin")
}
plugins {
    id("declarative-settings-plugin")
}

build("spring-boot-like") {
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