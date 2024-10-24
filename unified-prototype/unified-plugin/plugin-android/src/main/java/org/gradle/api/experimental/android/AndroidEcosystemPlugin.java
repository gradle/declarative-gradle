package org.gradle.api.experimental.android;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.android.application.StandaloneAndroidApplicationPlugin;
import org.gradle.api.experimental.android.library.StandaloneAndroidLibraryPlugin;
import org.gradle.api.experimental.buildinit.AbstractSpecContributingPlugin;
import org.gradle.api.experimental.buildinit.StaticProjectSpec;
import org.gradle.api.experimental.jvm.JvmEcosystemConventionsPlugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.plugins.software.RegistersSoftwareTypes;
import org.gradle.buildinit.specs.BuildInitSpec;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@RegistersSoftwareTypes({StandaloneAndroidApplicationPlugin.class, StandaloneAndroidLibraryPlugin.class})
public abstract class AndroidEcosystemPlugin extends AbstractSpecContributingPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings target) {
        registerSpecs();
        target.getPlugins().apply(JvmEcosystemConventionsPlugin.class);
        target.getDependencyResolutionManagement().getRepositories().google();
    }

    @Override
    protected List<BuildInitSpec> getSpecs() {
        return List.of(
            new StaticProjectSpec("android-application", "Declarative Android Application Project"),
            new StaticProjectSpec("android-application-basic-activity", "Declarative Android Application Project with Basic Activity"),
            new StaticProjectSpec("android-application-empty-activity", "Declarative Android Application Project with Empty Activity")
        );
    }
}
