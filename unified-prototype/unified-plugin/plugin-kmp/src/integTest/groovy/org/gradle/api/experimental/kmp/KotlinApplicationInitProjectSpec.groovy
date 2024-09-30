package org.gradle.api.experimental.kmp

import org.gradle.integtests.fixtures.AbstractProjectInitSpecification
import org.gradle.testkit.runner.GradleRunner
import org.junit.Ignore

/*
  TODO: This requires an upgrade to a published 0.1.14, to get the changes to the KMP project to use StaticKmpLibraryTargets and avoid
  errors about the PNDOC
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
//                .withArguments("build")
//                .forwardOutput()
//                .build()
//    }
}
