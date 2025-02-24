package org.gradle.api.experimental.android.application;

import com.android.build.api.dsl.ApplicationVariantDimension;
import org.gradle.api.Action;
import org.gradle.api.experimental.android.AndroidSoftwareBuildType;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface AndroidApplicationBuildType extends AndroidSoftwareBuildType {
    /**
     * @see ApplicationVariantDimension#getApplicationIdSuffix()
     */
    @Restricted
    Property<String> getApplicationIdSuffix();

    /**
     * @see ApplicationVariantDimension#getVersionNameSuffix()
     */
    @Restricted
    Property<String> getVersionNameSuffix();

    @Override
    @Nested
    AndroidApplicationDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super AndroidApplicationDependencies> action) {
        action.execute(getDependencies());
    }
}
