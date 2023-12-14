package org.gradle.experimental.settings;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;

import java.io.File;
import java.util.Set;

public interface ProjectContainer {
    ProjectSpecification subproject(String logicalPath);

    ProjectSpecification subproject(String logicalPath, String relativeDirPath);

    ProjectSpecification subproject(String logicalPath, Action<? super ProjectSpecification> action);

    ProjectSpecification subproject(String logicalPath, String relativeDirPath, Action<? super ProjectSpecification> action);

    void from(String path);

    File getDir();

    String getLogicalPath();
}
