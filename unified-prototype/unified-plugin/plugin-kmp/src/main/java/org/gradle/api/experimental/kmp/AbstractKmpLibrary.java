package org.gradle.api.experimental.kmp;

import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.internal.instantiation.InstantiatorFactory;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistry;

import javax.inject.Inject;

/**
 * This ideally should not exist and {@link KmpLibrary} should be expressed as only an interface,
 * however the KMP targets container needs some special handling for now.
 */
public abstract class AbstractKmpLibrary implements KmpLibrary {

    private final KmpTargetContainer targets;

    @Inject
    public AbstractKmpLibrary(
        Instantiator instantiator,
        InstantiatorFactory instantiatorFactory,
        ServiceRegistry services,
        CollectionCallbackActionDecorator decorator
    ) {
        Instantiator elementInstantiator = instantiatorFactory.decorateLenient(services);
        targets = instantiator.newInstance(KmpTargetContainer.class, instantiator, elementInstantiator, decorator);
        targets.registerBinding(KmpJvmTarget.class, KmpJvmTarget.class);
        targets.registerBinding(KmpJsTarget.class, KmpJsTarget.class);
    }

    @Override
    public KmpTargetContainer getTargets() {
        return targets;
    }
}
