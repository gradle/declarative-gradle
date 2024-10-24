package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.experimental.buildinit.AbstractSpecContributingPlugin;
import org.gradle.api.experimental.buildinit.StaticProjectSpec;
import org.gradle.api.experimental.java.StandaloneJavaApplicationPlugin;
import org.gradle.api.experimental.java.StandaloneJavaLibraryPlugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.plugins.software.RegistersSoftwareTypes;
import org.gradle.buildinit.specs.BuildInitSpec;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@RegistersSoftwareTypes({
        StandaloneJavaApplicationPlugin.class,
        StandaloneJavaLibraryPlugin.class,
        StandaloneJvmLibraryPlugin.class,
        StandaloneJvmApplicationPlugin.class
})
public abstract class JvmEcosystemPlugin extends AbstractSpecContributingPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings target) {
        registerSpecs();
        target.getPlugins().apply(JvmEcosystemConventionsPlugin.class);
    }

    @Override
    protected List<BuildInitSpec> getSpecs() {
        return List.of(
            new StaticProjectSpec("java-application", "Declarative Java Application Project")
        );
    }
}
