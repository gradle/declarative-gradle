package org.gradle.api.experimental.android;

import org.gradle.api.Action;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public abstract class AndroidTargets {
    private final AndroidTarget debug;
    private final AndroidTarget release;

    public AndroidTargets(AndroidTarget debug, AndroidTarget release) {
        this.debug = debug;
        this.release = release;
    }

    public AndroidTarget getDebug() {
        return debug;
    }

    @Configuring
    public void debug(Action<? super AndroidTarget> action) {
        action.execute(getDebug());
    }

    public AndroidTarget getRelease() {
        return release;
    }

    @Configuring
    public void release(Action<? super AndroidTarget> action) {
        action.execute(getRelease());
    }
}
