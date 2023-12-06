package org.gradle.experimental.settings;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;

import javax.annotation.Nullable;
import java.io.File;

public interface ProjectContainer {
    ProjectSpecification subproject(String path);

    ProjectSpecification subproject(String path, Action<? super ProjectSpecification> action);

    ProjectContainer directory(String path, Action<? super ProjectContainer> action);

    File getDir();

    String getLogicalPath();

    Property<Boolean> getAutodetect();
}
