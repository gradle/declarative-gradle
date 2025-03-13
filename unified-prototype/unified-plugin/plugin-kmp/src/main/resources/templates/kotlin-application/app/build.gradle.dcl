kotlinJvmApplication {
    mainClass = "org.example.app.AppKt"
    jvmArguments = listOf("-Xmx2G", "-XX:+HeapDumpOnOutOfMemoryError")

    dependencies {
        implementation(project(":utilities"))
    }
}
