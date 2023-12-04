pluginManagement {
    includeBuild("../declarative-settings-plugin")
}
plugins {
    id("declarative-settings-plugin")
}

build("top-level-projects") {
    autodetect = true
}