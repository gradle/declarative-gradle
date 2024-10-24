package org.gradle.experimental.settings.internal;

import org.gradle.api.Action;
import org.gradle.api.initialization.Settings;
import org.gradle.api.model.ObjectFactory;
import org.gradle.experimental.settings.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public abstract class DefaultWorkspaceSettings implements WorkspaceSettings {
    private final Settings settings;
    private final ProjectSpecificationFactory projectSpecificationFactory;
    private boolean projectsConfigured;
    private boolean buildConfigured = false;

    @Inject
    public DefaultWorkspaceSettings(Settings settings) {
        this.settings = settings;
        this.projectSpecificationFactory = new ProjectSpecificationFactoryImpl(settings.getRootDir());

        // this isn't technically correct, but it's the best we can do for the prototype.
        // presumably this would move to after the project layout is evaluated rather than after settings.
        settings.getGradle().settingsEvaluated(s -> {
            if (!projectsConfigured) {
                DefaultRootProjectSpecification spec = createRootProjectSpecification(settings);
                spec.autoDetectIfConfigured();
            }
        });
    }

    @NotNull
    private DefaultRootProjectSpecification createRootProjectSpecification(Settings settings) {
        return getObjectFactory().newInstance(DefaultRootProjectSpecification.class, settings, projectSpecificationFactory);
    }

    @Override
    public RootProjectSpecification layout(Action<? super RootProjectSpecification> action) {
        if (projectsConfigured) {
            throw new UnsupportedOperationException("The projects can only be configured once");
        }
        projectsConfigured = true;

        DefaultRootProjectSpecification spec = createRootProjectSpecification(settings);
        action.execute(spec);
        spec.autoDetectIfConfigured();

        return spec;
    }

    @Override
    public void build(Action<? super BuildSpecification> action) {
        if (buildConfigured) {
            throw new UnsupportedOperationException("The build can only be configured once");
        }
        buildConfigured = true;

        BuildSpecification spec = getObjectFactory().newInstance(BuildSpecification.class);
        action.execute(spec);
        if (spec.getName().isPresent()) {
            settings.getRootProject().setName(spec.getName().get());
        }
    }

    @Inject
    abstract protected ObjectFactory getObjectFactory();

    private class ProjectSpecificationFactoryImpl implements ProjectSpecificationFactory {
        private final Set<File> declaredProjectDirs = new HashSet<>();
        private final Set<String> declaredLogicalPaths = new HashSet<>();

        public ProjectSpecificationFactoryImpl(File rootDir) {
            declaredProjectDirs.add(rootDir);
        }

        @Override
        public boolean isLogicalPathOrDirectoryDeclared(String logicalPath, File dir) {
            return declaredLogicalPaths.contains(logicalPath) || declaredProjectDirs.contains(dir);
        }

        @Override
        public DefaultProjectSpecification create(Settings settings, File dir, String logicalPath, @Nullable ProjectContainer parent) {
            if (!declaredProjectDirs.add(dir)) {
                throw new IllegalArgumentException("Project directory '" + dir + "' has already been declared");
            }

            if (!declaredLogicalPaths.add(ProjectSpecification.logicalPathFromParent(logicalPath, parent))) {
                throw new IllegalArgumentException("Project logical path '" + logicalPath + "' has already been declared");
            }

            return getObjectFactory().newInstance(DefaultProjectSpecification.class,  settings, dir, logicalPath, parent, this);
        }
    }
}
