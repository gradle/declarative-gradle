jvmLibrary {
    dependencies {
        implementation("org.apache.commons:commons-lang3:3.14.0")
    }

    targets {
        java(21) {
            dependencies {
                // This library will only be available for Java 21 targets
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
