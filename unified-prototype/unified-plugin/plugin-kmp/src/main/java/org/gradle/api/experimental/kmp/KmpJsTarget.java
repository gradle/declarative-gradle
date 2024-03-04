package org.gradle.api.experimental.kmp;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

import java.util.Locale;

@Restricted
public interface KmpJsTarget extends KmpTarget {
    @Restricted
    Property<String> getEnvironment();

    enum JsEnvironment {
        NODE, BROWSER;

        public static JsEnvironment fromString(String name) {
            return switch (name.toUpperCase(Locale.getDefault())) {
                case "NODE" -> NODE;
                case "BROWSER" -> BROWSER;
                default -> throw new IllegalArgumentException("Unknown environment: " + name);
            };
        }
    }
}
