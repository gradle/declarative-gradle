package org.gradle.api.experimental.kmp;

import org.gradle.api.provider.Property;

public interface KmpJsTarget extends KmpTarget {

    Property<JsEnvironment> getEnvironment();

    enum JsEnvironment {
        NODE, BROWSER
    }

}
