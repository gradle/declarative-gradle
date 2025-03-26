javaApplication {
    // compile for 17
    javaVersion = 17
    mainClass = "com.example.App"
    jvmArguments = listOf("-Xmx2G", "-XX:+HeapDumpOnOutOfMemoryError")

    checkstyle {
        checkstyleVersion = "9.3"
        configDirectory = layout.settingsDirectory.dir("config")
        configFile = layout.settingsDirectory.file("config/checkstyle.xml")
    }

    dependencies {
        implementation(project(":java-util"))
        implementation("com.google.guava:guava:32.1.3-jre")
    }

    testing {
        // test on 21
        testJavaVersion = 21

        dependencies {
            implementation("org.junit.jupiter:junit-jupiter:5.10.2")
        }
    }
}
