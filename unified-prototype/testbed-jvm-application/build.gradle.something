jvmApplication {
    mainClass = "com.example.App"

    dependencies {
        implementation(project(":java-util"))
        implementation("com.google.guava:guava:32.1.3-jre")
    }

    targets {
        java(11) {
            dependencies {
                // Requires java 11
                implementation("org.hibernate.orm:hibernate-core:6.4.2.Final")
            }
        }
        java(17) {
            dependencies {
                // Requires java 17
                implementation("org.springframework.boot:spring-boot:3.2.2")
            }
        }
    }
}
