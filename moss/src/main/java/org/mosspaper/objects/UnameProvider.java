package org.mosspaper.objects;

import android.content.Context;

import org.mosspaper.DataService.State;

public enum UnameProvider implements DataProvider {
    INSTANCE;

    private String sysname;
    private String nodename;
    private String release;
    private String version;
    private String machine;

    public native void setUname();

    public void startup(Context context) { }

    public synchronized void update(State state) {
        /* We only need to gather this information once */
        if (sysname != null) {
            return;
        }
        setUname();
    }

    public void destroy(Context context) { }

    public boolean runWhenInvisible() {
        return false;
    }

    public String getSysname() {
        return sysname;
    }

    public String getNodename() {
        return nodename;
    }

    public String getRelease() {
        return release;
    }

    public String getMachine() {
        return machine;
    }

    static {
        System.loadLibrary("moss");
    }
}
