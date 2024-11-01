package org.gradle.api.experimental.android;

import com.android.build.api.dsl.BaseFlavor;
import com.android.build.api.dsl.CommonExtension;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.experimental.android.extensions.BaselineProfile;
import org.gradle.api.experimental.android.extensions.Compose;
import org.gradle.api.experimental.android.extensions.CoreLibraryDesugaring;
import org.gradle.api.experimental.android.extensions.Hilt;
import org.gradle.api.experimental.android.extensions.KotlinSerialization;
import org.gradle.api.experimental.android.extensions.Room;
import org.gradle.api.experimental.android.extensions.Secrets;
import org.gradle.api.experimental.android.extensions.testing.Testing;
import org.gradle.api.experimental.android.nia.Feature;
import org.gradle.api.experimental.android.extensions.Licenses;
import org.gradle.api.experimental.common.extensions.HasLinting;
import org.gradle.api.experimental.common.extensions.Lint;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface AndroidSoftware extends HasLinting {
    @Restricted
    Property<Integer> getTargetSdk();

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

    @Restricted
    Property<Boolean> getVectorDrawablesUseSupportLibrary();

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
        action.execute(getKotlinSerialization());
    }

    @Nested
    Compose getCompose();

    @Configuring
    default void compose(Action<? super Compose> action) {
        action.execute(getCompose());
    }

    @Nested
    CoreLibraryDesugaring getCoreLibraryDesugaring();

    @Configuring
    default void coreLibraryDesugaring(Action<? super CoreLibraryDesugaring> action) {
        action.execute(getCoreLibraryDesugaring());
    }

    @Nested
    Hilt getHilt();

    @Configuring
    default void hilt(Action<? super Hilt> action) {
        action.execute(getHilt());
    }

    @Nested
    Room getRoom();

    @Configuring
    default void room(Action<? super Room> action) {
        action.execute(getRoom());
    }

    @Nested
    Testing getTesting();

    @Configuring
    default void testing(Action<? super Testing> action) {
        action.execute(getTesting());
    }

    /**
     * Support for NiA convention projects defining features.
     * TODO:DG This is a temporary solution until we have a proper feature model.
     */
    @Nested
    Feature getFeature();

    @Configuring
    default void feature(Action<? super Feature> action) {
        action.execute(getFeature());
    }

    /**
     * Support for NiA projects using the com.google.android.gms.oss-licenses-plugin
     * TODO:DG This is a temporary solution until we have a better way of applying plugins
     */
    @Nested
    Licenses getLicenses();

    @Configuring
    default void licenses(Action<? super Licenses> action) {
        action.execute(getLicenses());
    }

    @Nested
    BaselineProfile getBaselineProfile();

    @Configuring
    default void baselineProfile(Action<? super BaselineProfile> action) {
        action.execute(getBaselineProfile());
    }

    @Override
    @Nested
    Lint getLint();

    @Configuring
    default void lint(Action<? super Lint> action) {
        action.execute(getLint());
    }

    /**
     * Applies the Secrets Gradle Plugin for Android (https://github.com/google/secrets-gradle-plugin).
     */
    @Nested
    Secrets getSecrets();

    @Configuring
    default void secrets(Action<? super Secrets> action) {
        action.execute(getSecrets());
    }

    @Nested
    NamedDomainObjectContainer<ExperimentalProperty> getExperimentalProperties();
}
