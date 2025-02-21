package org.gradle.api.experimental.android;

import org.gradle.api.Named;
import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface ExperimentalProperty extends Named {
    @Restricted
    Property<Boolean> getValue();
}
