package org.gradle.api.experimental.jvm;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;

/**
 * Applies some common conventions for the JVM ecosystems. In particular:
 * <p>
 * - Adds repository for Java toolchains.
 * - Adds Maven central for JVM libraries.
 */
public class JvmEcosystemConventionsPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings target) {
        target.getPlugins().apply("org.gradle.toolchains.foojay-resolver-convention");
        target.getDependencyResolutionManagement().getRepositories().mavenCentral();
    }
}
