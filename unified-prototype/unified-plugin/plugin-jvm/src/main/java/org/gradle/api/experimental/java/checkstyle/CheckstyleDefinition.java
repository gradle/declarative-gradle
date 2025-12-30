package org.gradle.api.experimental.java.checkstyle;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.internal.plugins.Definition;
import org.gradle.api.provider.Property;

public interface CheckstyleDefinition extends Definition<CheckstyleBuildModel> {
    Property<String> getCheckstyleVersion();

    DirectoryProperty getConfigDirectory();

    RegularFileProperty getConfigFile();
}
