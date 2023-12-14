package org.gradle.experimental.settings.internal;

import org.gradle.api.Action;
import org.gradle.api.initialization.Settings;
import org.gradle.api.model.ObjectFactory;
import org.gradle.experimental.settings.RootBuildSpecification;
import org.gradle.experimental.settings.WorkspaceSettings;

import javax.inject.Inject;

abstract public class DefaultWorkspaceSettings implements WorkspaceSettings {
    private final Settings settings;
    private boolean projectsConfigured;
    private boolean buildConfigured = false;

    @Inject
    public DefaultWorkspaceSettings(Settings settings) {
        this.settings = settings;
    }

    @Override
    public RootBuildSpecification layout(Action<? super RootBuildSpecification> action) {
        if (projectsConfigured) {
            throw new UnsupportedOperationException("The projects can only be configured once");
        }
        projectsConfigured = true;

        DefaultRootBuildSpecification spec = getObjectFactory().newInstance(DefaultRootBuildSpecification.class, settings);
        action.execute(spec);
        spec.autoDetectIfConfigured();

        return spec;
    }

    @Override
    public void build(String name, Action<? super Settings> action) {
        if (buildConfigured) {
            throw new UnsupportedOperationException("The build can only be configured once");
        }
        buildConfigured = true;
        settings.getRootProject().setName(name);
        action.execute(settings);
    }

    @Inject
    abstract protected ObjectFactory getObjectFactory();
}
