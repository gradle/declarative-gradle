package org.gradle.api.experimental.android;

import com.android.build.api.dsl.BaseFlavor;
import com.android.build.api.dsl.CommonExtension;
import org.gradle.api.Action;
import org.gradle.api.experimental.android.extensions.Compose;
import org.gradle.api.experimental.android.extensions.CoreLibraryDesugaring;
import org.gradle.api.experimental.android.extensions.Hilt;
import org.gradle.api.experimental.android.extensions.KotlinSerialization;
import org.gradle.api.experimental.android.extensions.Room;
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

    AndroidSoftwareBuildTypes getBuildTypes();

    AndroidSoftwareDependencies getDependencies();

    /**
     * Controls whether to set up Kotlin serialization, applying the plugins
     * and adding any necessary dependencies.
     */
    @Nested
    KotlinSerialization getKotlinSerialization();

    @Configuring
    default void kotlinSerialization(Action<? super KotlinSerialization> action) {
        KotlinSerialization kotlinSerialization = getKotlinSerialization();
        kotlinSerialization.getEnabled().set(true);
        action.execute(kotlinSerialization);
    }

    @Nested
    Compose getCompose();

    @Configuring
    default void compose(Action<? super Compose> action) {
        Compose compose = getCompose();
        compose.getEnabled().set(true);
        action.execute(compose);
    }

    @Nested
    CoreLibraryDesugaring getCoreLibraryDesugaring();

    @Configuring
    default void coreLibraryDesugaring(Action<? super CoreLibraryDesugaring> action) {
        CoreLibraryDesugaring coreLibraryDesugaring = getCoreLibraryDesugaring();
        coreLibraryDesugaring.getEnabled().set(true);
        action.execute(coreLibraryDesugaring);
    }

    @Nested
    Hilt getHilt();

    @Configuring
    default void hilt(Action<? super Hilt> action) {
        Hilt hilt = getHilt();
        hilt.getEnabled().set(true);
        action.execute(hilt);
    }

    @Nested
    Room getRoom();

    @Configuring
    default void room(Action<? super Room> action) {
        Room room = getRoom();
        room.getEnabled().set(true);
        action.execute(room);
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
        feature.getEnabled().set(true);
        action.execute(feature);
    }
}
