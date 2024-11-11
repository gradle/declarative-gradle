package org.gradle.api.experimental.buildinit;

import org.gradle.api.file.Directory;
import org.gradle.buildinit.specs.BuildInitConfig;
import org.gradle.buildinit.specs.BuildInitGenerator;
import org.gradle.util.ResourceLoader;

///**
// * A {@link BuildInitGenerator} that generates a Gradle build from a static template packaged
// * as resources files in the {@link #TEMPLATES_ROOT} directory.
// */
@SuppressWarnings("UnstableApiUsage")
public abstract class StaticBuildGenerator implements BuildInitGenerator {
    private static final String TEMPLATES_ROOT = "templates";

    @Override
    public void generate(BuildInitConfig config, Directory projectDir) {
        if (!(config.getBuildSpec() instanceof StaticBuildSpec projectSpec)) {
            throw new IllegalArgumentException("Unknown project type: " + config.getBuildSpec().getDisplayName() + " (" + config.getBuildSpec().getClass().getName() + ")");
        }

        String templatePath = TEMPLATES_ROOT + "/" + projectSpec.getType();
        ResourceLoader resourceLoader = new ResourceLoader();

        try {
            resourceLoader.extractDirectoryFromResources(templatePath, projectDir.getAsFile());
        } catch (Exception e) {
            throw new RuntimeException("Error extracting resources for: '" + projectSpec.getDisplayName() + "' from: '" + templatePath + "'!", e);
        }
    }
}
