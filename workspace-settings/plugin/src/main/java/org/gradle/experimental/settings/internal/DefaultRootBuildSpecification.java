package org.gradle.experimental.settings.internal;

import org.gradle.api.initialization.Settings;
import org.gradle.experimental.settings.RootBuildSpecification;

import javax.inject.Inject;

abstract public class DefaultRootBuildSpecification extends AbstractProjectContainer implements RootBuildSpecification {
    @Inject
    public DefaultRootBuildSpecification(Settings settings) {
        super(settings, settings.getRootDir(), "", null);
        getAutodetect().convention(true);
    }

    @Override
    public String getLogicalPath() {
        return ":";
    }
}
