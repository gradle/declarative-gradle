package org.gradle.api.experimental.kmp.testing;

import org.gradle.api.experimental.common.BasicDependencies;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * Represents a set of dependencies for a {@link TestSuite} in a KMP project.
 */
@Restricted
public interface TestingDependencies extends BasicDependencies {}
