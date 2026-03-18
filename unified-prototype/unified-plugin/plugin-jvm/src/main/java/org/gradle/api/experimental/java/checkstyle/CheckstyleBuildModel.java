package org.gradle.api.experimental.java.checkstyle;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.features.binding.BuildModel;

public interface CheckstyleBuildModel extends BuildModel {
    Property<String> getCheckstyleVersion();

    DirectoryProperty getConfigDirectory();

    RegularFileProperty getConfigFile();

    ConfigurableFileCollection getClasspath();
}
