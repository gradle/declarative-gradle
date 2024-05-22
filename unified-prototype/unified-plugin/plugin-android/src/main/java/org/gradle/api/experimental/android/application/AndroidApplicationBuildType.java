package org.gradle.api.experimental.android.application;

import com.android.build.api.dsl.ApplicationVariantDimension;
import org.gradle.api.Action;
import org.gradle.api.experimental.android.extensions.Minify;
import org.gradle.api.experimental.common.ApplicationDependencies;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface AndroidApplicationBuildType {
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

    /**
     * Dependencies for this build type.
     */
    @Nested
    ApplicationDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super ApplicationDependencies> action) {
        action.execute(getDependencies());
    }

    @Nested
    Minify getMinify();

    @Configuring
    default void minify(Action<? super Minify> action) {
        action.execute(getMinify());
    }
}
