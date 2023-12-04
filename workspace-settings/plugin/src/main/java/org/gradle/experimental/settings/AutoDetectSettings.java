package org.gradle.experimental.settings;

/**
 * Configures patterns for auto-detecting projects based on the directory name.
 */
public interface AutoDetectSettings {
    void include(String directoryPattern);
    void exclude(String directoryPattern);
}
