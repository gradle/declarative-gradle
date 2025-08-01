package org.gradle.api.experimental.java.checkstyle;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.internal.plugins.BuildModel;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface CheckstyleBuildModel extends BuildModel {

    DirectoryProperty getConfigDirectory();

    RegularFileProperty getConfigFile();

    ConfigurableFileCollection getClasspath();
}
