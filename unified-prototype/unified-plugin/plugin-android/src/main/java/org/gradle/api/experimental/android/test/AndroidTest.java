package org.gradle.api.experimental.android.test;

import com.android.build.api.dsl.BaseFlavor;
import com.android.build.api.dsl.CommonExtension;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.experimental.android.ExperimentalProperty;
import org.gradle.api.experimental.android.extensions.BaselineProfile;
import org.gradle.api.experimental.android.extensions.testing.AndroidTestDependencies;
import org.gradle.api.experimental.android.extensions.testing.TestOptions;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

// TODO: This probably shouldn't extend AndroidSoftware, as it does not
// represent a project the produces production software.  There should be a
// common AbstractAndroidPlugin that AndroidSoftwarePlugin and this plugin
// both extend that folds these commonalities back into it
public interface AndroidTest {
    /**
     * JDK version to use for compilation.
     */
    @Restricted
    Property<Integer> getJdkVersion();

    /**
     * @see CommonExtension#getNamespace()
     */
    @Restricted
    Property<String> getNamespace();

    /**
     * @see CommonExtension#getCompileSdk()
     */
    @Restricted
    Property<Integer> getCompileSdk();

    /**
     * @see BaseFlavor#getMinSdk()
     */
    @Restricted
    Property<Integer> getMinSdk();

    @Restricted
    Property<Integer> getTargetSdk();

    /**
     * Flag to enable/disable generation of the `BuildConfig` class.
     *
     * Default value is `false`.
     */
    @Restricted
    Property<Boolean> getBuildConfig();

    @Nested
    BaselineProfile getBaselineProfile();

    @Configuring
    default void baselineProfile(Action<? super BaselineProfile> action) {
        action.execute(getBaselineProfile());
    }

    @Nested
    NamedDomainObjectContainer<ExperimentalProperty> getExperimentalProperties();

    @Nested
    AndroidTestDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super AndroidTestDependencies> action) {
        action.execute(getDependencies());
    }

    @Nested
    TestOptions getTestOptions();

    @Configuring
    default void testOptions(Action<? super TestOptions> action) {
        action.execute(getTestOptions());
    }

    @Restricted
    Property<String> getTargetProjectPath();
}
