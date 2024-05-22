package org.gradle.api.experimental.android.extensions;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface Hilt {
    /**
     * Internal property purposely not exposed to the DSL.
     */
    Property<Boolean> getEnabled();
}
