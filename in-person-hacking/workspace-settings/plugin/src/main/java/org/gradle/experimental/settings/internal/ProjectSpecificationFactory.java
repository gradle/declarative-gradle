package org.gradle.experimental.settings.internal;

import org.gradle.api.initialization.Settings;
import org.gradle.experimental.settings.ProjectContainer;

import javax.annotation.Nullable;
import java.io.File;

public interface ProjectSpecificationFactory {
    boolean isLogicalPathOrDirectoryDeclared(String logicalPath, File dir);
    DefaultProjectSpecification create(Settings settings, File dir, String logicalPath, @Nullable ProjectContainer parent);
}
