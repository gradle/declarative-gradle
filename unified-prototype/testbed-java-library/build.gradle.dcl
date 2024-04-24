javaLibrary {
    javaVersion = 21

    dependencies {
        implementation(project(":java-util"))
        implementation("com.google.guava:guava:32.1.3-jre")
    }
}
