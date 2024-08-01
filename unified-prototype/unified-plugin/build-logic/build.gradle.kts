plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.gradle.publish:plugin-publish-plugin:1.2.1")
}

gradlePlugin {
    plugins {
        create("build-logic.build-update-utils") {
            id = "build-logic.build-update-utils"
            implementationClass = "buildlogic.BuildUpdateUtilsPlugin"
        }
    }
}
