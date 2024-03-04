package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer;
import org.gradle.declarative.dsl.model.annotations.Adding;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;
import org.gradle.internal.reflect.Instantiator;

import javax.inject.Inject;

@Restricted
public abstract class KmpTargetContainer extends DefaultPolymorphicDomainObjectContainer<KmpTarget> {
    @Inject
    public KmpTargetContainer(Instantiator instantiator, Instantiator elementInstantiator, CollectionCallbackActionDecorator callbackDecorator) {
        super(KmpTarget.class, instantiator, elementInstantiator, callbackDecorator);
    }

    @Adding
    public void jvm() {
        maybeCreate("jvm", KmpJvmTarget.class);
    }

    @Adding
    public void jvm(String name) {
        maybeCreate(name, KmpJvmTarget.class);
    }

    @Configuring
    public void jvm(Action<? super KmpJvmTarget> action) {
        KmpJvmTarget jvm = maybeCreate("jvm", KmpJvmTarget.class);
        action.execute(jvm);
    }

    // Can't @Configuring this - a configuring function may not accept any other parameters
    // public void jvm(String name, Action<? super KmpJvmTarget> action) {
    //    create(name, KmpJvmTarget.class, action);
    //}

    @Adding
    public void js() {
        maybeCreate("js", KmpJsTarget.class);
    }

    @Adding
    public void js(String name) {
        maybeCreate(name, KmpJsTarget.class);
    }

    @Configuring
    public void js(Action<? super KmpJsTarget> action) {
        KmpJsTarget js = maybeCreate("js", KmpJsTarget.class);
        action.execute(js);
    }

    // Can't @Configuring this - a configuring function may not accept any other parameters
    //public void js(String name, Action<? super KmpJsTarget> action) {
    //    create(name, KmpJsTarget.class, action);
    //}
}
