pluginManagement {
    includeBuild("../declarative-settings-plugin")
}
plugins {
    id("declarative-settings-plugin")
}

build("platforms-subprojects") {
    directory("platforms") {
        subproject("jvm") {
            autodetect = true
        }
        subproject("ide") {
            autodetect = true
        }
    }
}