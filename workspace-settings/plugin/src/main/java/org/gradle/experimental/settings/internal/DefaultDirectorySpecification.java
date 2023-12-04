package org.gradle.experimental.settings.internal;

import org.gradle.api.initialization.Settings;
import org.gradle.experimental.settings.ProjectContainer;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.io.File;

abstract public class DefaultDirectorySpecification extends AbstractProjectContainer {
    @Inject
    public DefaultDirectorySpecification(Settings settings, String relativePath, @Nullable ProjectContainer parent) {
        super(settings, new File(parent.getDir(), relativePath), "", parent);
    }

    @Override
    public String getLogicalPath() {
        return parent.getLogicalPath();
    }
}
