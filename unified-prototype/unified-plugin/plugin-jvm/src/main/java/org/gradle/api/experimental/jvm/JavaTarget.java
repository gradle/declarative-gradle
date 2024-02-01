package org.gradle.api.experimental.jvm;

import org.gradle.api.Named;

public abstract class JavaTarget implements JvmTarget, Named {

    private final int javaVersion;

    public JavaTarget(int javaVersion) {
        this.javaVersion = javaVersion;
    }

    @Override
    public String getName() {
        return "java" + javaVersion;
    }

    public int getJavaVersion() {
        return javaVersion;
    }

}
