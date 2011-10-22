package org.moss.objects;

import org.moss.Common;
import org.moss.DataService.State;

import android.content.Context;
import android.os.SystemClock;

public class Realtime extends AbsMossObject implements MossObject, DataProvider {

    /**
     * Display the systems time since the system was booted, including deep
     * sleep.
     */
    public Realtime() {
        realTime = SystemClock.elapsedRealtime();
    }

    public DataProvider getDataProvider() {
        return this;
    }

    public void startup(Context context) { }

    public void update(State state) {
        realTime = SystemClock.elapsedRealtime();
    }

    public void destroy(Context context) { }

    public boolean runWhenInvisible() {
        return false;
    }

    @Override
    public String toString() {
        return Common.formatSeconds(realTime / 1000);
    }

    long realTime;
}

