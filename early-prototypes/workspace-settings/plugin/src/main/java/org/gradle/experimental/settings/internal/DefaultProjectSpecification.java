package org.gradle.experimental.settings.internal;

import org.gradle.api.initialization.Settings;
import org.gradle.experimental.settings.ProjectContainer;
import org.gradle.experimental.settings.ProjectSpecification;

import javax.inject.Inject;
import java.io.File;

import static org.gradle.experimental.settings.ProjectSpecification.logicalPathFromParent;

public abstract class DefaultProjectSpecification extends AbstractProjectContainer implements ProjectSpecification {
    @Inject
    public DefaultProjectSpecification(Settings settings, File dir, String logicalPath, ProjectContainer parent, ProjectSpecificationFactory projectSpecificationFactory) {
        super(settings, dir, logicalPath, parent, projectSpecificationFactory);
        getAutodetect().convention(((AbstractProjectContainer)parent).getAutodetect());
    }

    @Override
    public String getLogicalPath() {
        return logicalPathFromParent(logicalPathRelativeToParent, parent);
    }
}
