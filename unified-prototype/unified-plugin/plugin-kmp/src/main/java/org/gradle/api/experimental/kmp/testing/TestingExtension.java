package org.gradle.api.experimental.kmp.testing;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;

import java.util.Map;

/**
 * A DSL block that allows for configuring testing in a KMP project.
 */
public interface TestingExtension {
    @Nested
    TestingDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super TestingDependencies> action) {
        action.execute(getDependencies());
    }

    @Nested
    TestSuite getUnitTest();

    @Configuring
    default void unitTest(Action<? super TestSuite> action) {
        action.execute(getUnitTest());
    }

    @Nested
    TestSuite getFunctionalTest();

    @Configuring
    default void functionalTest(Action<? super TestSuite> action) {
        action.execute(getFunctionalTest());
    }

    default Map<String, TestSuite> getTestSuites() {
        return Map.of("unitTest", getUnitTest(), "functionalTest", getFunctionalTest());
    }
}
