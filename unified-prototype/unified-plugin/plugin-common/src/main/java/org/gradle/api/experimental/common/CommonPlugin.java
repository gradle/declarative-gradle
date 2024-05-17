package org.gradle.api.experimental.common;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;

public class CommonPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings target) {
        // Currently does nothing, as this plugin is only used for its classes.
        // In fact, no other plugin applies this one.
    }
}
