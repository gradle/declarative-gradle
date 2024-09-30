package org.gradle.api.experimental.buildinit;

//import org.gradle.api.file.Directory;
//import org.gradle.buildinit.projectspecs.InitProjectConfig;
import org.gradle.buildinit.projectspecs.InitProjectGenerator;
//import org.gradle.util.ResourceLoader;

///**
// * An {@link InitProjectGenerator} that generates a project from a static template packaged
// * as resources files in the {@link #TEMPLATES_ROOT} directory.
// */
@SuppressWarnings("UnstableApiUsage")
public abstract class StaticProjectGenerator {}
//implements InitProjectGenerator {
//    private static final String TEMPLATES_ROOT = "templates";
//
//    @Override
//    public void generate(InitProjectConfig config, Directory projectDir) {
//        if (!(config.getProjectSpec() instanceof StaticProjectSpec projectSpec)) {
//            throw new IllegalArgumentException("Unknown project type: " + config.getProjectSpec().getDisplayName() + " (" + config.getProjectSpec().getClass().getName() + ")");
//        }
//
//        String templatePath = TEMPLATES_ROOT + "/" + projectSpec.getTemplatePath();
//        ResourceLoader resourceLoader = new ResourceLoader();
//
//        try {
//            resourceLoader.extractDirectoryFromResources(templatePath, projectDir.getAsFile());
//        } catch (Exception e) {
//            throw new RuntimeException("Error extracting resources for: '" + projectSpec.getDisplayName() + "' from: '" + templatePath + "'!", e);
//        }
//    }
//}
