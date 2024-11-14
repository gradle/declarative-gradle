androidApp {
    namespace = "org.example.app"
    dependenciesDcl {
        implementation("org.apache.commons:commons-text:1.11.0")
        implementation(project(":utilities"))
    }
}
