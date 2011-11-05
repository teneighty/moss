package org.mosspaper.objects;

import android.util.Log;

import org.mosspaper.DataService.State;

public class UpdateManager {

    public UpdateManager(DataProvider data) {
        this.data = data;
        reset();
    }

    public void setInterval(long newinterval) {
        if (interval == -1) {
            this.interval = newinterval;
        } else {
            this.interval = Math.min(this.interval, newinterval);
        }
    }

    public long getInterval() {
        return this.interval;
    }

    public void reset() {
        this.interval = -1;
        this.lastUpdate = 0L;
    }

    public void update(State state) {
        if (!state.isVisible() && !data.runWhenInvisible()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (Math.abs(now - lastUpdate) > interval) {
            data.update(state);
            lastUpdate = now;
        }
    }

    private DataProvider data;
    private long lastUpdate;
    private long interval;

    static final String TAG = "UpdateManager";
}
