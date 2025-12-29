package org.gradle.api.experimental.jvm;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectSet;
import org.gradle.api.internal.collections.DomainObjectCollectionFactory;
import org.gradle.declarative.dsl.model.annotations.Adding;
import org.gradle.declarative.dsl.model.annotations.HiddenInDefinition;
import org.gradle.internal.instantiation.InstantiatorFactory;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistry;

import javax.inject.Inject;

public class JvmTargetContainer {
    private final NamedDomainObjectSet<JvmTarget> container;
    private final Instantiator elementInstantiator;

    @Inject
    public JvmTargetContainer(InstantiatorFactory instantiatorFactory, ServiceRegistry serviceRegistry, DomainObjectCollectionFactory domainObjectCollectionFactory) {
        this.container = domainObjectCollectionFactory.newNamedDomainObjectSet(JvmTarget.class);
        this.elementInstantiator = instantiatorFactory.decorateLenient(serviceRegistry);
    }

    @Adding
    public JvmTarget java(int version) {
        return java(version, it -> {});
    }

    @Adding
    public JvmTarget java(int version, Action<? super JvmTarget> action) {
        JavaTarget element = elementInstantiator.newInstance(JavaTarget.class, version);
        container.add(element);
        action.execute(element);
        return element;
    }

    @HiddenInDefinition
    public NamedDomainObjectSet<JvmTarget> getStore() {
        return container;
    }
}
