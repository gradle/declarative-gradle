pluginManagement {
    includeBuild("../declarative-settings-plugin")
}
plugins {
    id("declarative-settings-plugin")
}

build("single-project")