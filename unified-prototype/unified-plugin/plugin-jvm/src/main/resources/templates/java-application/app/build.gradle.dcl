javaApplication {
    mainClass = "org.example.app.App"
    jvmArguments = listOf("-Xmx2G", "-XX:+HeapDumpOnOutOfMemoryError")

    dependencies {
        implementation("org.apache.commons:commons-text:1.11.0")
        implementation(project(":utilities"))
    }
}
