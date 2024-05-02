package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer;
import org.gradle.declarative.dsl.model.annotations.Adding;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;
import org.gradle.internal.instantiation.InstantiatorFactory;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistry;

import javax.inject.Inject;

@Restricted
public abstract class KmpApplicationTargetContainer extends DefaultPolymorphicDomainObjectContainer<KmpApplicationTarget> {
    @Inject
    public KmpApplicationTargetContainer(Instantiator instantiator, InstantiatorFactory instantiatorFactory, CollectionCallbackActionDecorator callbackDecorator, ServiceRegistry services) {
        super(KmpApplicationTarget.class, instantiator, instantiatorFactory.decorateLenient(services), callbackDecorator);
        registerBinding(KmpApplicationJvmTarget.class, KmpApplicationJvmTarget.class);
        registerBinding(KmpApplicationNodeJsTarget.class, KmpApplicationNodeJsTarget.class);
        registerBinding(KmpApplicationNativeTarget.class, KmpApplicationNativeTarget.class);
    }

    @Adding
    public void jvm() {
        maybeCreate("jvm", KmpApplicationJvmTarget.class);
    }

    @Configuring
    public void jvm(Action<? super KmpApplicationJvmTarget> action) {
        KmpApplicationJvmTarget target = maybeCreate("jvm", KmpApplicationJvmTarget.class);
        action.execute(target);
    }

    @Adding
    public void nodeJs() {
        maybeCreate("nodeJs", KmpApplicationNodeJsTarget.class);
    }

    @Configuring
    public void nodeJs(Action<? super KmpApplicationNodeJsTarget> action) {
        KmpApplicationNodeJsTarget target = maybeCreate("nodeJs", KmpApplicationNodeJsTarget.class);
        action.execute(target);
    }

    @Adding
    public void macOsArm64() {
        maybeCreate("macOsArm64", KmpApplicationNativeTarget.class);
    }

    @Configuring
    public void macOsArm64(Action<? super KmpApplicationNativeTarget> action) {
        KmpApplicationNativeTarget target = maybeCreate("macOsArm64", KmpApplicationNativeTarget.class);
        action.execute(target);
    }
}
