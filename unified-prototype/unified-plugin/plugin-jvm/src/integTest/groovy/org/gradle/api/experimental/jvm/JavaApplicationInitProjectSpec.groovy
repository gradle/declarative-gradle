//file:noinspection GroovyAssignabilityCheck
package org.gradle.api.experimental.jvm

import org.gradle.integtests.fixtures.AbstractProjectInitSpecification

class JavaApplicationInitProjectSpec extends AbstractProjectInitSpecification {
    @Override
    String getEcosystemPluginId() {
        return "org.gradle.experimental.jvm-ecosystem"
    }

    @Override
    String getProjectSpecType() {
        return "java-application"
    }
}
