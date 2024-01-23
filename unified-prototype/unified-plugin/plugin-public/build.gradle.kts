plugins {
    id("java-gradle-plugin")
}

description = """
    Contains the actual Unified Plugin class and pulls in each standalone plugin implementations
    for each ecosystem. 
"""

dependencies {
    api(project(":plugin-android"))
    api(project(":plugin-jvm"))
    api(project(":plugin-kmp"))
}

gradlePlugin {
    plugins {
        create("unified-plugin") {
            id = "org.gradle.unified-prototype"
            implementationClass = "org.gradle.api.experimental.UnifiedPlugin"
        }
    }
}