package org.gradle.experimental.settings.internal;

import org.gradle.api.initialization.Settings;
import org.gradle.experimental.settings.RootProjectSpecification;

import javax.inject.Inject;

abstract public class DefaultRootProjectSpecification extends AbstractProjectContainer implements RootProjectSpecification {
    @Inject
    public DefaultRootProjectSpecification(Settings settings) {
        super(settings, settings.getRootDir(), "", null);
        getAutodetect().convention(true);
    }

    @Override
    public String getLogicalPath() {
        return ":";
    }
}
