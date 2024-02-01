package org.gradle.api.experimental.jvm;

import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.internal.instantiation.InstantiatorFactory;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistry;

import javax.inject.Inject;

/**
 * The public DSL interface for a declarative JVM library.
 */
public abstract class AbstractJvmLibrary implements JvmLibrary {

    private final JvmTargetContainer targets;

    @Inject
    public AbstractJvmLibrary(
        Instantiator instantiator,
        InstantiatorFactory instantiatorFactory,
        ServiceRegistry services,
        CollectionCallbackActionDecorator decorator
    ) {
        Instantiator elementInstantiator = instantiatorFactory.decorateLenient(services);
        targets = instantiator.newInstance(JvmTargetContainer.class, instantiator, elementInstantiator, decorator);
    }

    @Override
    public JvmTargetContainer getTargets() {
        return targets;
    }
}
