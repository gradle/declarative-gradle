package org.gradle.api.experimental.java.checkstyle;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.internal.plugins.HasBuildModel;
import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;
import org.jspecify.annotations.NonNull;

public interface CheckstyleDefinition extends HasBuildModel<@NonNull CheckstyleBuildModel> {
    @Restricted
    Property<String> getCheckstyleVersion();

    @Restricted
    DirectoryProperty getConfigDirectory();

    @Restricted
    RegularFileProperty getConfigFile();
}
