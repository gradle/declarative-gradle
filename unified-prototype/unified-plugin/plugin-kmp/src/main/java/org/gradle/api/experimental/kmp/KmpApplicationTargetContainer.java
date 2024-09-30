package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer;
import org.gradle.internal.instantiation.InstantiatorFactory;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistry;

import javax.inject.Inject;

public abstract class KmpApplicationTargetContainer extends DefaultPolymorphicDomainObjectContainer<KmpApplicationTarget> implements StaticKmpApplicationTargets {
    @Inject
    public KmpApplicationTargetContainer(Instantiator instantiator, InstantiatorFactory instantiatorFactory, CollectionCallbackActionDecorator callbackDecorator, ServiceRegistry services) {
        super(KmpApplicationTarget.class, instantiator, instantiatorFactory.decorateLenient(services), callbackDecorator);
        registerBinding(KmpApplicationJvmTarget.class, KmpApplicationJvmTarget.class);
        registerBinding(KmpApplicationNodeJsTarget.class, KmpApplicationNodeJsTarget.class);
        registerBinding(KmpApplicationNativeTarget.class, KmpApplicationNativeTarget.class);
    }

    @Override
    public void jvm() {
        maybeCreate("jvm", KmpApplicationJvmTarget.class);
    }

    @Override
    public void jvm(Action<? super KmpApplicationJvmTarget> action) {
        KmpApplicationJvmTarget target = maybeCreate("jvm", KmpApplicationJvmTarget.class);
        action.execute(target);
    }

    @Override
    public void nodeJs() {
        maybeCreate("nodeJs", KmpApplicationNodeJsTarget.class);
    }

    @Override
    public void nodeJs(Action<? super KmpApplicationNodeJsTarget> action) {
        KmpApplicationNodeJsTarget target = maybeCreate("nodeJs", KmpApplicationNodeJsTarget.class);
        action.execute(target);
    }

    @Override
    public void macOsArm64() {
        maybeCreate("macOsArm64", KmpApplicationNativeTarget.class);
    }

    @Override
    public void macOsArm64(Action<? super KmpApplicationNativeTarget> action) {
        KmpApplicationNativeTarget target = maybeCreate("macOsArm64", KmpApplicationNativeTarget.class);
        action.execute(target);
    }
}
