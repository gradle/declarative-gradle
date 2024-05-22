package org.gradle.api.experimental.android;

import com.android.build.api.dsl.BaseFlavor;
import com.android.build.api.dsl.CommonExtension;
import org.gradle.api.Action;
import org.gradle.api.experimental.android.extensions.Compose;
import org.gradle.api.experimental.android.extensions.CoreLibraryDesugaring;
import org.gradle.api.experimental.android.extensions.Hilt;
import org.gradle.api.experimental.android.extensions.KotlinSerialization;
import org.gradle.api.experimental.android.extensions.testing.Testing;
import org.gradle.api.experimental.android.nia.Feature;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface AndroidSoftware {
    /**
     * @see CommonExtension#getCompileSdk()
     */
    @Restricted
    Property<Integer> getCompileSdk();

    /**
     * @see CommonExtension#getNamespace()
     */
    @Restricted
    Property<String> getNamespace();

    /**
     * @see BaseFlavor#getMinSdk()
     */
    @Restricted
    Property<Integer> getMinSdk();

    /**
     * JDK version to use for compilation.
     */
    @Restricted
    Property<Integer> getJdkVersion();

    @Nested
    AndroidSoftwareBuildTypes getBuildTypes();

    @Nested
    AndroidSoftwareDependencies getDependencies();

    /**
     * Controls whether or not to set up Kotlin serialization, applying the plugins
     * and adding any necessary dependencies.
     */
    @Nested
    KotlinSerialization getKotlinSerialization();

    @Configuring
    default void kotlinSerialization(Action<? super KotlinSerialization> action) {
        KotlinSerialization kotlinSerialization = getKotlinSerialization();
        action.execute(kotlinSerialization);
        kotlinSerialization.getEnabled().set(true);
    }

    @Nested
    Compose getCompose();

    @Configuring
    default void compose(Action<? super Compose> action) {
        Compose compose = getCompose();
        action.execute(compose);
        compose.getEnabled().set(true);
    }

    @Nested
    CoreLibraryDesugaring getCoreLibraryDesugaring();

    @Configuring
    default void coreLibraryDesugaring(Action<? super CoreLibraryDesugaring> action) {
        CoreLibraryDesugaring coreLibraryDesugaring = getCoreLibraryDesugaring();
        action.execute(coreLibraryDesugaring);
        coreLibraryDesugaring.getEnabled().set(true);
    }

    @Nested
    Hilt getHilt();

    @Configuring
    default void hilt(Action<? super Hilt> action) {
        Hilt hilt = getHilt();
        action.execute(hilt);
        hilt.getEnabled().set(true);
    }

    @Nested
    Testing getTesting();

    @Configuring
    default void testing(Action<? super Testing> action) {
        action.execute(getTesting());
    }

    /**
     * Support for NiA convention projects defining features.
     * TODO: This is a temporary solution until we have a proper feature model.
     */
    @Nested
    Feature getFeature();

    @Configuring
    default void feature(Action<? super Feature> action) {
        Feature feature = getFeature();
        action.execute(feature);
        feature.getEnabled().set(true);
    }

}
