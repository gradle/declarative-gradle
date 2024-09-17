package org.gradle.api.experimental.kmp

import org.gradle.integtests.fixtures.AbstractProjectInitSpecification
import org.gradle.testkit.runner.GradleRunner
import org.junit.Ignore

/*
  TODO: Figure out why this is failing with:
   Cannot use org.gradle.api.internal.AbstractNamedDomainObjectContainer<org.gradle.api.experimental.kmp.KmpLibraryTarget!>! as a container type in fun org.gradle.api.experimental.kmp.KmpLibraryTargetContainer.configure(groovy.lang.Closure<(raw) kotlin.Any!>!): org.gradle.api.internal.AbstractNamedDomainObjectContainer<org.gradle.api.experimental.kmp.KmpLibraryTarget!>!
*/
@Ignore
class KotlinApplicationInitProjectSpec { //extends AbstractProjectInitSpecification {
//    @Override
//    protected String getEcosystemPluginId() {
//        return "org.gradle.experimental.kmp-ecosystem"
//    }
//
//    @Override
//    protected String getProjectSpecType() {
//        return "declarative-kotlin-j-v-m-application-project"
//    }
//
//    @Override
//    protected void validateBuiltProject() {
//        result = GradleRunner.create()
//                .withProjectDir(projectDir)
//                .withArguments(":app:run")
//                .forwardOutput()
//                .build()
//
//        assert result.output.contains("Hello World!")
//    }
//
//    @Override
//    protected void canBuildGeneratedProject() {
//        result = GradleRunner.create()
//                .withProjectDir(projectDir)
//                .withPluginClasspath()
//                .withArguments(["build", "-Xdoclint:none"]) // Suppress repetitive warning for missing javadoc
//                .forwardOutput()
//                .build()
//    }
}
