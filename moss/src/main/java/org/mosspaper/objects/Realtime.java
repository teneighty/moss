package org.mosspaper.objects;

import org.mosspaper.Common;

import android.os.SystemClock;

public class Realtime extends AbsMossObject implements MossObject {

    /**
     * Display the systems time since the system was booted, including deep
     * sleep.
     */
    public Realtime() { }

    @Override
    public String toString() {
        return Common.formatSeconds(SystemClock.elapsedRealtime() / 1000);
    }
}

