package org.gradle.api.experimental.jvm;

import org.gradle.api.Action;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.internal.DefaultNamedDomainObjectSet;
import org.gradle.declarative.dsl.model.annotations.Adding;
import org.gradle.declarative.dsl.model.annotations.Restricted;
import org.gradle.internal.instantiation.InstantiatorFactory;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistry;

import javax.inject.Inject;

public class JvmTargetContainer extends DefaultNamedDomainObjectSet<JvmTarget> {

    private final Instantiator elementInstantiator;

    @Inject
    public JvmTargetContainer(Instantiator instantiator, InstantiatorFactory instantiatorFactory, ServiceRegistry serviceRegistry, CollectionCallbackActionDecorator callbackDecorator) {
        super(JvmTarget.class, instantiator, callbackDecorator);
        this.elementInstantiator = instantiatorFactory.decorateLenient(serviceRegistry);
    }

    @Adding
    public JvmTarget java(int version) {
        return java(version, it -> {});
    }

    @Adding
    public JvmTarget java(int version, Action<? super JvmTarget> action) {
        JavaTarget element = elementInstantiator.newInstance(JavaTarget.class, version);
        add(element);
        action.execute(element);
        return element;
    }
}
