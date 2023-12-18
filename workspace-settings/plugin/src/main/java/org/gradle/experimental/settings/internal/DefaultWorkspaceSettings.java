package org.gradle.experimental.settings.internal;

import org.gradle.api.Action;
import org.gradle.api.initialization.Settings;
import org.gradle.api.model.ObjectFactory;
import org.gradle.experimental.settings.BuildSpecification;
import org.gradle.experimental.settings.RootProjectSpecification;
import org.gradle.experimental.settings.WorkspaceSettings;

import javax.inject.Inject;

abstract public class DefaultWorkspaceSettings implements WorkspaceSettings {
    private final Settings settings;
    private boolean projectsConfigured;
    private boolean buildConfigured = false;

    @Inject
    public DefaultWorkspaceSettings(Settings settings) {
        this.settings = settings;

        // this isn't technically correct, but it's the best we can do for the prototype.
        // presumably this would move to after the project layout is evaluated rather than after settings.
        settings.getGradle().settingsEvaluated(s -> {
            if (!projectsConfigured) {
                DefaultRootProjectSpecification spec = getObjectFactory().newInstance(DefaultRootProjectSpecification.class, settings);
                spec.autoDetectIfConfigured();
            }
        });
    }

    @Override
    public RootProjectSpecification layout(Action<? super RootProjectSpecification> action) {
        if (projectsConfigured) {
            throw new UnsupportedOperationException("The projects can only be configured once");
        }
        projectsConfigured = true;

        DefaultRootProjectSpecification spec = getObjectFactory().newInstance(DefaultRootProjectSpecification.class, settings);
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
}
