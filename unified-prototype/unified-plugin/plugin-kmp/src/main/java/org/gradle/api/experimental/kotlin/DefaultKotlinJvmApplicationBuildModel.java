package org.gradle.api.experimental.kotlin;

import org.gradle.api.plugins.JavaApplication;

public class DefaultKotlinJvmApplicationBuildModel extends DefaultKotlinJvmLibraryBuildModel implements KotlinJvmApplicationBuildModel {
    private JavaApplication javaApplicationExtension;

    @Override
    public JavaApplication getJavaApplicationExtension() {
        return javaApplicationExtension;
    }

    public void setJavaApplicationExtension(JavaApplication javaApplicationExtension) {
        this.javaApplicationExtension = javaApplicationExtension;
    }
}
