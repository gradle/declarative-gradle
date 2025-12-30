package org.gradle.api.experimental.android.test;

import com.android.build.api.dsl.BaseFlavor;
import com.android.build.api.dsl.CommonExtension;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.experimental.android.ExperimentalProperty;
import org.gradle.api.experimental.android.extensions.BaselineProfile;
import org.gradle.api.experimental.android.extensions.testing.AndroidTestDependencies;
import org.gradle.api.experimental.android.extensions.testing.TestOptions;
import org.gradle.api.internal.plugins.Definition;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

// TODO: This probably shouldn't extend AndroidSoftware, as it does not
// represent a project the produces production software.  There should be a
// common AbstractAndroidPlugin that AndroidSoftwarePlugin and this plugin
// both extend that folds these commonalities back into it
public interface AndroidTest extends Definition<AndroidTestBuildModel> {
    /**
     * JDK version to use for compilation.
     */
    Property<Integer> getJdkVersion();

    /**
     * @see CommonExtension#getNamespace()
     */
    Property<String> getNamespace();

    /**
     * @see CommonExtension#getCompileSdk()
     */
    Property<Integer> getCompileSdk();

    /**
     * @see BaseFlavor#getMinSdk()
     */
    Property<Integer> getMinSdk();

    Property<Integer> getTargetSdk();

    /**
     * Flag to enable/disable generation of the `BuildConfig` class.
     *
     * Default value is `false`.
     */
    Property<Boolean> getBuildConfig();

    @Nested
    BaselineProfile getBaselineProfile();

    @Nested
    NamedDomainObjectContainer<ExperimentalProperty> getExperimentalProperties();

    @Nested
    AndroidTestDependencies getDependencies();

    @Nested
    TestOptions getTestOptions();

    Property<String> getTargetProjectPath();
}
