package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.PolymorphicDomainObjectContainer;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer;
import org.gradle.api.internal.collections.DomainObjectCollectionFactory;
import org.gradle.declarative.dsl.model.annotations.HiddenInDefinition;
import org.gradle.internal.instantiation.InstantiatorFactory;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistry;

import javax.inject.Inject;

public abstract class KmpApplicationTargetContainer implements StaticKmpApplicationTargets {
    private final DefaultPolymorphicDomainObjectContainer<KmpApplicationTarget> container;

    @Inject
    public KmpApplicationTargetContainer(Instantiator instantiator, InstantiatorFactory instantiatorFactory, DomainObjectCollectionFactory domainObjectCollectionFactory, CollectionCallbackActionDecorator callbackDecorator, ServiceRegistry services) {
        this.container = (DefaultPolymorphicDomainObjectContainer<KmpApplicationTarget>) domainObjectCollectionFactory.newPolymorphicDomainObjectContainer(KmpApplicationTarget.class);
        this.container.registerBinding(KmpApplicationJvmTarget.class, KmpApplicationJvmTarget.class);
        this.container.registerBinding(KmpApplicationNodeJsTarget.class, KmpApplicationNodeJsTarget.class);
        this.container.registerBinding(KmpApplicationNativeTarget.class, KmpApplicationNativeTarget.class);
    }

    @Override
    public void jvm(Action<? super KmpApplicationJvmTarget> action) {
        KmpApplicationJvmTarget target = container.maybeCreate("jvm", KmpApplicationJvmTarget.class);
        action.execute(target);
    }

    @Override
    public void nodeJs(Action<? super KmpApplicationNodeJsTarget> action) {
        KmpApplicationNodeJsTarget target = container.maybeCreate("nodeJs", KmpApplicationNodeJsTarget.class);
        action.execute(target);
    }

    @Override
    public void macOsArm64(Action<? super KmpApplicationNativeTarget> action) {
        KmpApplicationNativeTarget target = container.maybeCreate("macOsArm64", KmpApplicationNativeTarget.class);
        action.execute(target);
    }

    @HiddenInDefinition
    public PolymorphicDomainObjectContainer<KmpApplicationTarget> getStore() {
        return container;
    }
}
