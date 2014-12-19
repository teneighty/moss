package org.mosspaper.objects;

import android.content.Context;

import org.mosspaper.DataService.State;

import java.util.List;
import java.util.ArrayList;

public enum PortMonProvider implements DataProvider {

    INSTANCE;

    class Range {
        public Range(int s, int e) {
            this.s = s;
            this.e = e;
        }
        int s;
        int e;
    }

    PortMonProvider() {
        this.started = false;
        this.ranges = new ArrayList<Range>();
    }

    public int registerMonitor(String var, int startPort, int endPort) {
        addRange(new Range(startPort, endPort));
        return monitemlookup(var);
    }

    private void addRange(Range r) {
        for (Range ra : ranges) {
            if (ra.s == r.s && ra.e == r.e) {
                return;
            }
        }
        ranges.add(r);
    }

    @Override
    public void startup(Context context) { 
        if (!started) {
            moninit();
            for (Range r : ranges) {
                monadd(r.s, r.e);
            }
            started = true;
        }
        /* set the initial state */
        update(null);
    }

    @Override
    public void update(State state) {
        monupdate();
    }

    @Override
    public void destroy(Context context) {
        mondestroy();
        started = false;
    }

    @Override
    public boolean runWhenInvisible() {
        return false;
    }

    private List <Range> ranges;
    private boolean started = false;
    private native void moninit();
    private native void monupdate();
    private native void mondestroy();
    private native int monitemlookup(String str);
    private native boolean monadd(int startPort, int endPort);
    public native String monpeek(int startPort, int endPort, int item, int index);

    static {
        System.loadLibrary("moss");
    }
}
