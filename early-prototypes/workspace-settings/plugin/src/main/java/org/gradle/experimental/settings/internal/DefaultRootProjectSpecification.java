package org.gradle.experimental.settings.internal;

import org.gradle.api.initialization.Settings;
import org.gradle.experimental.settings.RootProjectSpecification;

import javax.inject.Inject;
import java.io.File;
import java.util.function.Predicate;

public abstract class DefaultRootProjectSpecification extends AbstractProjectContainer implements RootProjectSpecification {
    @Inject
    public DefaultRootProjectSpecification(Settings settings, ProjectSpecificationFactory projectSpecificationFactory) {
        super(settings, settings.getRootDir(), "", null, projectSpecificationFactory);
        getAutodetect().convention(true);
    }

    @Override
    public String getLogicalPath() {
        return ":";
    }
}
