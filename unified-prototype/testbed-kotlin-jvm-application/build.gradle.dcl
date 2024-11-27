kotlinJvmApplication {
    javaVersion = 21
    mainClass = "com.example.AppKt"

    dependencies {
        implementation(project(":kotlin-jvm-util"))
        implementation("com.google.guava:guava:32.1.3-jre")
    }

    testing {
        dependencies {
            implementation("org.junit.jupiter:junit-jupiter:5.10.2")
        }
    }
}
