package org.gradle.api.experimental.jvm;

import org.gradle.api.plugins.JavaApplication;

public class DefaultJavaApplicationBuildModel extends DefaultJavaBuildModel implements JavaApplicationBuildModel {
    private JavaApplication javaApplicationExtension;

    @Override
    public JavaApplication getJavaApplicationExtension() {
        return javaApplicationExtension;
    }

    public void setJavaApplicationExtension(JavaApplication javaApplicationExtension) {
        this.javaApplicationExtension = javaApplicationExtension;
    }
}
