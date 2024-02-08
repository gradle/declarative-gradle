package org.gradle.api.experimental.android;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;

public abstract class AndroidTargets {
    private final AndroidTarget debug;
    private final AndroidTarget release;

    public AndroidTargets(AndroidTarget debug, AndroidTarget release) {
        this.debug = debug;
        this.release = release;
    }

    @Nested
    public AndroidTarget getDebug() {
        return debug;
    }

    public void debug(Action<? super AndroidTarget> action) {
        action.execute(getDebug());
    }

    @Nested
    public AndroidTarget getRelease() {
        return release;
    }

    public void release(Action<? super AndroidTarget> action) {
        action.execute(getRelease());
    }
}
