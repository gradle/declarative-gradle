package org.gradle.api.experimental.jvm.buildinit;

//import java.util.List;

import org.gradle.api.experimental.buildinit.StaticProjectGenerator;
import org.gradle.api.experimental.buildinit.StaticProjectSpec;
import org.gradle.buildinit.projectspecs.InitProjectGenerator;
import org.gradle.buildinit.projectspecs.InitProjectSpec;
import org.gradle.buildinit.projectspecs.InitProjectSource;

/**
 * A {@link InitProjectSource} of project specifications for JVM projects.
 */
@SuppressWarnings("UnstableApiUsage")
public final class JVMProjectSource {}
//implements InitProjectSource {
//    @Override
//    public List<InitProjectSpec> getProjectSpecs() {
//        return List.of(
//            new StaticProjectSpec("java-application", "Declarative Java Application Project")
//        );
//    }
//
//    @Override
//    public Class<? extends InitProjectGenerator> getProjectGenerator() {
//        return StaticProjectGenerator.class;
//    }
//}
