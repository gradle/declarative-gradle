package org.gradle.api.experimental.java.checkstyle;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;

public interface HasCheckstyle {
    @Nested
    CheckstyleDefinition getCheckstyle();

    @Configuring
    default void checkstyle(Action<? super CheckstyleDefinition> action) {
        action.execute(getCheckstyle());
    }
}
