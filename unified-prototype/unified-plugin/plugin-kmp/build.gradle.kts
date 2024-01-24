plugins {
    `kotlin-dsl`
}

description = "Implements the declarative KMP DSL prototype"

dependencies {
    implementation(project(":plugin-common"))
}

gradlePlugin {
    plugins {
        create("kmp-plugin") {
            id = "org.gradle.kmp-prototype"
            implementationClass = "org.gradle.api.experimental.kmp.StandaloneKmpPlugin"
        }
    }
}