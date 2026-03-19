package org.gradle.api.experimental.android;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.android.application.AndroidApplication;
import org.gradle.api.experimental.android.application.StandaloneAndroidApplicationPlugin;
import org.gradle.api.experimental.android.library.AndroidLibrary;
import org.gradle.api.experimental.android.library.StandaloneAndroidLibraryPlugin;
import org.gradle.api.experimental.android.test.AndroidTest;
import org.gradle.api.experimental.android.test.StandaloneAndroidTestPlugin;
import org.gradle.api.experimental.jvm.JvmEcosystemConventionsPlugin;
import org.gradle.api.initialization.Settings;
import org.gradle.buildinit.specs.internal.BuildInitSpecRegistry;
import org.gradle.features.annotations.RegistersProjectFeatures;

import javax.inject.Inject;

@SuppressWarnings("UnstableApiUsage")
@RegistersProjectFeatures({StandaloneAndroidApplicationPlugin.class, StandaloneAndroidLibraryPlugin.class, StandaloneAndroidTestPlugin.class})
public abstract class AndroidEcosystemPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings settings) {
        settings.getPlugins().apply(JvmEcosystemConventionsPlugin.class);
        settings.getDependencyResolutionManagement().getRepositories().google();

        settings.getDefaults().add(
            StandaloneAndroidApplicationPlugin.ANDROID_APPLICATION,
            AndroidApplication.class,
            definition -> {
                AndroidBindingSupport.bindCommon(settings.getProviders(), definition);

                definition.getDependencyGuard().getEnabled().convention(false);
                definition.getFirebase().getEnabled().convention(false);
                definition.getFirebase().getVersion().convention("32.4.0");
                definition.getBuildTypes().getDebug().getApplicationIdSuffix().convention((String) null);
                definition.getBuildTypes().getRelease().getApplicationIdSuffix().convention((String) null);
                definition.getFlavors().getEnabled().convention(false);
                definition.getViewBinding().getEnabled().convention(false);
                definition.getDataBinding().getEnabled().convention(false);
            }
        );

        settings.getDefaults().add(
            StandaloneAndroidLibraryPlugin.ANDROID_LIBRARY,
            AndroidLibrary.class,
            definition -> {
                AndroidBindingSupport.bindCommon(settings.getProviders(), definition);

                definition.getProtobuf().getEnabled().convention(false);
                definition.getBuildConfig().convention(false);
            }
        );

        settings.getDefaults().add(
            StandaloneAndroidTestPlugin.ANDROID_TEST,
            AndroidTest.class,
            definition -> {
                definition.getMinSdk().convention(AndroidBindingSupport.DEFAULT_MIN_ANDROID_SDK);
                definition.getBuildConfig().convention(false);
                definition.getBaselineProfile().getEnabled().convention(false);
                definition.getBaselineProfile().getUseConnectedDevices().convention(true);
                definition.getTestOptions().getIncludeAndroidResources().convention(false);
                definition.getTestOptions().getReturnDefaultValues().convention(false);
            }
        );
    }

    @Inject
    protected abstract BuildInitSpecRegistry getBuildInitSpecRegistry();
}
