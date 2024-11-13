package org.gradle.api.experimental.android;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.buildinit.StaticBuildGenerator;
import org.gradle.api.experimental.buildinit.StaticBuildSpec;
import org.gradle.api.initialization.Settings;
import org.gradle.buildinit.specs.internal.BuildInitSpecRegistry;

import javax.inject.Inject;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public abstract class AndroidEcosystemInitPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings settings) {
        // TODO: Add something for generating AGP-based templates
        getBuildInitSpecRegistry().register(StaticBuildGenerator.class, List.of(
                new StaticBuildSpec("android-application", "Android application with Android libraries"),
                new StaticBuildSpec("android-application-basic-activity", "Android Application with a basic Activity"),
                new StaticBuildSpec("android-application-empty-activity", "Android Application with an empty Activity"),
                new StaticBuildSpec("android-application-agp-preview", "Android Application using Official AGP Software Types Preview")
        ));
    }

    @Inject
    protected abstract BuildInitSpecRegistry getBuildInitSpecRegistry();
}
