package org.gradle.experimental.settings.internal;

import org.gradle.api.initialization.Settings;
import org.gradle.experimental.settings.ProjectContainer;
import org.gradle.experimental.settings.ProjectSpecification;

import javax.inject.Inject;
import java.io.File;

abstract public class DefaultProjectSpecification extends AbstractProjectContainer implements ProjectSpecification {
    @Inject
    public DefaultProjectSpecification(Settings settings, File dir, String logicalPath, ProjectContainer parent) {
        super(settings, dir, logicalPath, parent);
        getAutodetect().convention(((AbstractProjectContainer)parent).getAutodetect());
    }

    @Override
    public String getLogicalPath() {
        if (parent == null) {
            throw new IllegalStateException();
        } else {
            String parentLogicalPath = parent.getLogicalPath();
            if (parentLogicalPath.equals(LOGICAL_PATH_SEPARATOR)) {
                return LOGICAL_PATH_SEPARATOR + logicalPathName;
            } else {
                return parentLogicalPath + LOGICAL_PATH_SEPARATOR + logicalPathName;
            }
        }
    }
}
