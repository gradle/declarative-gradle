package org.gradle.api.experimental.android;

import com.h0tk3y.kotlin.staticObjectNotation.Configuring;
import com.h0tk3y.kotlin.staticObjectNotation.Restricted;
import org.gradle.api.Action;

@Restricted
public abstract class AndroidTargets {
    private final AndroidTarget debug;
    private final AndroidTarget release;

    public AndroidTargets(AndroidTarget debug, AndroidTarget release) {
        this.debug = debug;
        this.release = release;
    }

    @Restricted
    public AndroidTarget getDebug() {
        return debug;
    }

    @Configuring
    public void debug(Action<? super AndroidTarget> action) {
        action.execute(getDebug());
    }

    @Restricted
    public AndroidTarget getRelease() {
        return release;
    }

    @Configuring
    public void release(Action<? super AndroidTarget> action) {
        action.execute(getRelease());
    }
}
