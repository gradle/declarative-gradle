package org.gradle.api.experimental.jvm;

import org.gradle.api.plugins.JavaPluginExtension;

public class DefaultJavaBuildModel implements JavaBuildModel {
    private JavaPluginExtension javaPluginExtension;

    @Override
    public JavaPluginExtension getJavaPluginExtension() {
        return javaPluginExtension;
    }

    public void setJavaPluginExtension(JavaPluginExtension javaPluginExtension) {
        this.javaPluginExtension = javaPluginExtension;
    }
}
