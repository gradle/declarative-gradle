package org.gradle.api.experimental.android.application;

import com.android.build.api.dsl.ApplicationVariantDimension;
import org.gradle.api.experimental.android.AndroidSoftwareBuildType;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

public interface AndroidApplicationBuildType extends AndroidSoftwareBuildType {
    /**
     * @see ApplicationVariantDimension#getApplicationIdSuffix()
     */
    Property<String> getApplicationIdSuffix();

    /**
     * @see ApplicationVariantDimension#getVersionNameSuffix()
     */
    Property<String> getVersionNameSuffix();

    @Override
    @Nested
    AndroidApplicationDependencies getDependencies();
}
