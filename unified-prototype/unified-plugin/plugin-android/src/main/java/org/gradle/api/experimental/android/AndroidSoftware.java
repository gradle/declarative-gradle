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

public interface AndroidSoftware extends HasLinting {
    Property<Integer> getTargetSdk();

    /**
     * @see CommonExtension#getCompileSdk()
     */
    Property<Integer> getCompileSdk();

    /**
     * @see CommonExtension#getNamespace()
     */
    Property<String> getNamespace();

    /**
     * @see BaseFlavor#getMinSdk()
     */
    Property<Integer> getMinSdk();

    /**
     * JDK version to use for compilation.
     */
    Property<Integer> getJdkVersion();

    Property<Boolean> getVectorDrawablesUseSupportLibrary();

    AndroidSoftwareBuildTypes getBuildTypes();

    AndroidSoftwareDependencies getDependencies();

    /**
     * Controls whether to set up Kotlin serialization, applying the plugins
     * and adding any necessary dependencies.
     */
    @Nested
    KotlinSerialization getKotlinSerialization();

    @Nested
    Compose getCompose();

    @Nested
    CoreLibraryDesugaring getCoreLibraryDesugaring();

    @Nested
    Hilt getHilt();

    @Nested
    Room getRoom();

    @Nested
    Testing getTesting();

    /**
     * Support for NiA convention projects defining features.
     * TODO:DG This is a temporary solution until we have a proper feature model.
     */
    @Nested
    Feature getFeature();

    /**
     * Support for NiA projects using the com.google.android.gms.oss-licenses-plugin
     * TODO:DG This is a temporary solution until we have a better way of applying plugins
     */
    @Nested
    Licenses getLicenses();

    @Nested
    BaselineProfile getBaselineProfile();

    @Override
    @Nested
    Lint getLint();

    /**
     * Applies the Secrets Gradle Plugin for Android (https://github.com/google/secrets-gradle-plugin).
     */
    @Nested
    Secrets getSecrets();

    @Nested
    NamedDomainObjectContainer<ExperimentalProperty> getExperimentalProperties();
}
