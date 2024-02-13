package org.gradle.api.experimental.android;

import com.h0tk3y.kotlin.staticObjectNotation.Configuring;
import com.h0tk3y.kotlin.staticObjectNotation.Restricted;
import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;

@Restricted
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

    @Configuring
    public void debug(Action<? super AndroidTarget> action) {
        action.execute(getDebug());
    }

    @Nested
    public AndroidTarget getRelease() {
        return release;
    }

    @Configuring
    public void release(Action<? super AndroidTarget> action) {
        action.execute(getRelease());
    }
}
