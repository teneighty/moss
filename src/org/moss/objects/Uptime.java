package org.moss.objects;

import org.moss.Common;

import android.os.SystemClock;

public class Uptime extends AbsMossObject implements MossObject {

    /**
     * Display the systems uptime. On android this does not include when the
     * system enters deep sleep. @see Realtime.
     */
    public Uptime() { }

    @Override
    public String toString() {
        return Common.formatSeconds(SystemClock.uptimeMillis() / 1000);
    }
}

