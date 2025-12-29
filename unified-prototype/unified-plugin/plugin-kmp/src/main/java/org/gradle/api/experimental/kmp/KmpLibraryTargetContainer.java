package org.gradle.api.experimental.kmp;

import org.gradle.api.Action;
import org.gradle.api.PolymorphicDomainObjectContainer;
import org.gradle.declarative.dsl.model.annotations.HiddenInDefinition;
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer;
import org.gradle.api.internal.collections.DomainObjectCollectionFactory;

import javax.inject.Inject;

public abstract class KmpLibraryTargetContainer implements StaticKmpLibraryTargets {
    private final DefaultPolymorphicDomainObjectContainer<KmpLibraryTarget> container;

    @Inject
    public KmpLibraryTargetContainer(DomainObjectCollectionFactory domainObjectCollectionFactory) {
        this.container = (DefaultPolymorphicDomainObjectContainer<KmpLibraryTarget>) domainObjectCollectionFactory.newPolymorphicDomainObjectContainer(KmpLibraryTarget.class);
        this.container.registerBinding(KmpLibraryJvmTarget.class, KmpLibraryJvmTarget.class);
        this.container.registerBinding(KmpLibraryNodeJsTarget.class, KmpLibraryNodeJsTarget.class);
        this.container.registerBinding(KmpLibraryNativeTarget.class, KmpLibraryNativeTarget.class);
    }

    @Override
    public void jvm() {
        container.maybeCreate("jvm", KmpLibraryJvmTarget.class);
    }

    @Override
    public void jvm(Action<? super KmpLibraryJvmTarget> action) {
        KmpLibraryJvmTarget target = container.maybeCreate("jvm", KmpLibraryJvmTarget.class);
        action.execute(target);
    }

    @Override
    public void nodeJs() {
        container.maybeCreate("nodeJs", KmpLibraryNodeJsTarget.class);
    }

    @Override
    public void nodeJs(Action<? super KmpLibraryNodeJsTarget> action) {
        KmpLibraryNodeJsTarget target = container.maybeCreate("nodeJs", KmpLibraryNodeJsTarget.class);
        action.execute(target);
    }

    @Override
    public void macOsArm64() {
        container.maybeCreate("macOsArm64", KmpLibraryNativeTarget.class);
    }

    @Override
    public void macOsArm64(Action<? super KmpLibraryNativeTarget> action) {
        KmpLibraryNativeTarget target = container.maybeCreate("macOsArm64", KmpLibraryNativeTarget.class);
        action.execute(target);
    }

    @HiddenInDefinition
    public PolymorphicDomainObjectContainer<KmpLibraryTarget> getStore() {
        return container;
    }
}
