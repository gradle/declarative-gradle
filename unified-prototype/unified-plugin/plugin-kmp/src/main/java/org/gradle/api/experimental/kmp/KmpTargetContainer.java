package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer;
import org.gradle.declarative.dsl.model.annotations.Adding;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;
import org.gradle.internal.reflect.Instantiator;

@Restricted
public class KmpTargetContainer extends DefaultPolymorphicDomainObjectContainer<KmpTarget> {
    public KmpTargetContainer(Instantiator instantiator, Instantiator elementInstantiator, CollectionCallbackActionDecorator callbackDecorator) {
        super(KmpTarget.class, instantiator, elementInstantiator, callbackDecorator);
    }

    @Adding
    public void jvm() {
        create("jvm", KmpJvmTarget.class);
    }

    @Adding
    public void jvm(String name) {
        create(name, KmpJvmTarget.class);
    }

    @Configuring
    public void jvm(Action<? super KmpJvmTarget> action) {
        create("jvm", KmpJvmTarget.class, action);
    }

    public void jvm(String name, Action<? super KmpJvmTarget> action) {
        create(name, KmpJvmTarget.class, action);
    }

    public void js() {
        create("js", KmpJsTarget.class);
    }

    public void js(String name) {
        create(name, KmpJsTarget.class);
    }

    @Configuring
    public void js(Action<? super KmpJsTarget> action) {
        create("js", KmpJsTarget.class, action);
    }

    public void js(String name, Action<? super KmpJsTarget> action) {
        create(name, KmpJsTarget.class, action);
    }
}
