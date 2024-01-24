plugins {
    `kotlin-dsl`
}

description = "Implements the declarative Android DSL prototype"

dependencies {
    implementation(project(":plugin-common"))
}

gradlePlugin {
    plugins {
        create("android-plugin") {
            id = "org.gradle.android-prototype"
            implementationClass = "org.gradle.api.experimental.android.StandaloneAndroidPlugin"
        }
    }
}