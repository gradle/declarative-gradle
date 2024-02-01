package org.gradle.api.experimental.jvm;

import org.gradle.api.Action;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.internal.DefaultNamedDomainObjectSet;
import org.gradle.internal.reflect.Instantiator;

public class JvmTargetContainer extends DefaultNamedDomainObjectSet<JvmTarget> {

    private final Instantiator elementInstantiator;

    public JvmTargetContainer(Instantiator instantiator, Instantiator elementInstantiator, CollectionCallbackActionDecorator callbackDecorator) {
        super(JvmTarget.class, instantiator, callbackDecorator);
        this.elementInstantiator = elementInstantiator;
    }

    public void java(int version) {
        java(version, it -> {});
    }

    public void java(int version, Action<? super JvmTarget> action) {
        JavaTarget element = elementInstantiator.newInstance(JavaTarget.class, version);
        add(element);
        action.execute(element);
    }

}
