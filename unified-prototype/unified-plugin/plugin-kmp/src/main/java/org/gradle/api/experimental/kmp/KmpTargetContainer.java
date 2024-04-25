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
public abstract class KmpTargetContainer extends DefaultPolymorphicDomainObjectContainer<KmpTarget> {
    @Inject
    public KmpTargetContainer(Instantiator instantiator, InstantiatorFactory instantiatorFactory, CollectionCallbackActionDecorator callbackDecorator, ServiceRegistry services) {
        super(KmpTarget.class, instantiator, instantiatorFactory.decorateLenient(services), callbackDecorator);
        registerBinding(KmpJvmTarget.class, KmpJvmTarget.class);
        registerBinding(KmpJsTarget.class, KmpJsTarget.class);
    }

    @Adding
    public void jvm() {
        maybeCreate("jvm", KmpJvmTarget.class);
    }

    @Configuring
    public void jvm(Action<? super KmpJvmTarget> action) {
        KmpJvmTarget jvm = maybeCreate("jvm", KmpJvmTarget.class);
        action.execute(jvm);
    }

    @Adding
    public void js() {
        maybeCreate("js", KmpJsTarget.class);
    }

    @Configuring
    public void js(Action<? super KmpJsTarget> action) {
        KmpJsTarget js = maybeCreate("js", KmpJsTarget.class);
        action.execute(js);
    }
}
