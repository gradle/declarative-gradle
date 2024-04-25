package org.gradle.api.experimental.android.library;

import com.android.build.api.dsl.BuildType;
import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface AndroidLibraryBuildType {

    /**
     * @see BuildType#isMinifyEnabled()
     */
    @Restricted
    Property<Boolean> getMinifyEnabled();

    /**
     * Dependencies for this build type.
     */
    @Nested
    AndroidLibraryDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super AndroidLibraryDependencies> action) {
        action.execute(getDependencies());
    }
}
