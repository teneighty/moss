package org.moss.objects;

import org.moss.Common;
import org.moss.DataService.State;

import android.content.Context;
import android.os.SystemClock;

public class Uptime extends AbsMossObject implements MossObject, DataProvider {

    /**
     * Display the systems uptime. On android this does not include when the
     * system enters deep sleep. @see Realtime.
     */
    public Uptime() {
        uptime = SystemClock.uptimeMillis();
    }

    public DataProvider getDataProvider() {
        return this;
    }

    public void startup(Context context) { }

    public void update(State state) {
        uptime = SystemClock.uptimeMillis();
    }

    public void destroy(Context context) { }

    public boolean runWhenInvisible() {
        return false;
    }

    @Override
    public String toString() {
        return Common.formatSeconds(uptime / 1000);
    }

    long uptime;
}

