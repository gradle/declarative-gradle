package org.gradle.api.experimental.android;

import org.gradle.api.Named;
import org.gradle.api.provider.Property;

public interface ExperimentalProperty extends Named {
    Property<Boolean> getValue();
}
