package org.mosspaper.objects;

import android.util.Log;

import org.mosspaper.DataService.State;

public class UpdateManager {

    public UpdateManager(DataProvider data) {
        this.data = data;
        this.lastUpdate = 0L;
        this.interval = -1;
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

    public void update(State state) {
        if (!state.isVisible() && !data.runWhenInvisible()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (Math.abs(now - lastUpdate) > interval) {
            Log.i(TAG, data.getClass().getName());
            data.update(state);
            lastUpdate = now;
        }
    }

    private DataProvider data;
    private long lastUpdate;
    private long interval;

    static final String TAG = "UpdateManager";
}
