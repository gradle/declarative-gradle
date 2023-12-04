pluginManagement {
    includeBuild("../declarative-settings-plugin")
}
plugins {
    id("declarative-settings-plugin")
}

build("subprojects-dir") {
    directory("subprojects") {
        autodetect = true
    }
}