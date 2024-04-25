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
public abstract class KmpLibraryTargetContainer extends DefaultPolymorphicDomainObjectContainer<KmpLibraryTarget> {
    @Inject
    public KmpLibraryTargetContainer(Instantiator instantiator, InstantiatorFactory instantiatorFactory, CollectionCallbackActionDecorator callbackDecorator, ServiceRegistry services) {
        super(KmpLibraryTarget.class, instantiator, instantiatorFactory.decorateLenient(services), callbackDecorator);
        registerBinding(KmpLibraryJvmTarget.class, KmpLibraryJvmTarget.class);
        registerBinding(KmpLibraryNodeJsTarget.class, KmpLibraryNodeJsTarget.class);
    }

    @Adding
    public void jvm() {
        maybeCreate("jvm", KmpLibraryJvmTarget.class);
    }

    @Configuring
    public void jvm(Action<? super KmpLibraryJvmTarget> action) {
        KmpLibraryJvmTarget jvm = maybeCreate("jvm", KmpLibraryJvmTarget.class);
        action.execute(jvm);
    }

    @Adding
    public void nodeJs() {
        maybeCreate("nodeJs", KmpLibraryNodeJsTarget.class);
    }

    @Configuring
    public void nodeJs(Action<? super KmpLibraryNodeJsTarget> action) {
        KmpLibraryNodeJsTarget js = maybeCreate("nodeJs", KmpLibraryNodeJsTarget.class);
        action.execute(js);
    }
}
