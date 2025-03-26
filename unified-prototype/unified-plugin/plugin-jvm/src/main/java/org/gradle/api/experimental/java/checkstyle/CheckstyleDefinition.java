package org.gradle.api.experimental.java.checkstyle;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface CheckstyleDefinition {
    @Restricted
    Property<String> getCheckstyleVersion();

    @Restricted
    DirectoryProperty getConfigDirectory();

    @Restricted
    RegularFileProperty getConfigFile();
}
