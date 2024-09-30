package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer;
import org.gradle.internal.instantiation.InstantiatorFactory;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistry;

import javax.inject.Inject;

public abstract class KmpLibraryTargetContainer extends DefaultPolymorphicDomainObjectContainer<KmpLibraryTarget> implements StaticKmpLibraryTargets {
    @Inject
    public KmpLibraryTargetContainer(Instantiator instantiator, InstantiatorFactory instantiatorFactory, CollectionCallbackActionDecorator callbackDecorator, ServiceRegistry services) {
        super(KmpLibraryTarget.class, instantiator, instantiatorFactory.decorateLenient(services), callbackDecorator);
        registerBinding(KmpLibraryJvmTarget.class, KmpLibraryJvmTarget.class);
        registerBinding(KmpLibraryNodeJsTarget.class, KmpLibraryNodeJsTarget.class);
        registerBinding(KmpLibraryNativeTarget.class, KmpLibraryNativeTarget.class);
    }

    @Override
    public void jvm() {
        maybeCreate("jvm", KmpLibraryJvmTarget.class);
    }

    @Override
    public void jvm(Action<? super KmpLibraryJvmTarget> action) {
        KmpLibraryJvmTarget target = maybeCreate("jvm", KmpLibraryJvmTarget.class);
        action.execute(target);
    }

    @Override
    public void nodeJs() {
        maybeCreate("nodeJs", KmpLibraryNodeJsTarget.class);
    }

    @Override
    public void nodeJs(Action<? super KmpLibraryNodeJsTarget> action) {
        KmpLibraryNodeJsTarget target = maybeCreate("nodeJs", KmpLibraryNodeJsTarget.class);
        action.execute(target);
    }

    @Override
    public void macOsArm64() {
        maybeCreate("macOsArm64", KmpLibraryNativeTarget.class);
    }

    @Override
    public void macOsArm64(Action<? super KmpLibraryNativeTarget> action) {
        KmpLibraryNativeTarget target = maybeCreate("macOsArm64", KmpLibraryNativeTarget.class);
        action.execute(target);
    }
}
