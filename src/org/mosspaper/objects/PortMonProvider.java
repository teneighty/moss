package org.mosspaper.objects;

import android.content.Context;

import org.mosspaper.DataService.State;

public enum PortMonProvider implements DataProvider {

    INSTANCE;

    public int registerMonitor(String var, int startPort, int endPort) {
        if (!started) {
            moninit();
            started = true;
        }
        if (!monadd(startPort, endPort)) {
            /* TODO: throw an error or something */
        }
        return monitemlookup(var);
    }

    @Override
    public void startup(Context context) { 
        if (!started) {
            moninit();
            started = true;
        }
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

    private boolean started = false;
    private native void moninit();
    private native void monupdate();
    private native void mondestroy();
    private native int monitemlookup(String str);
    private native boolean monadd(int startPort, int endPort);
    public native String monpeek(int startPort, int endPort, int item, int index);

    static {
        System.loadLibrary("portmon");
    }
}
