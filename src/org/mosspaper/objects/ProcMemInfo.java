package org.mosspaper.objects;

import android.content.Context;
import android.util.Log;

import org.mosspaper.DataService.State;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public enum ProcMemInfo implements DataProvider {

    INSTANCE;

    static final String TAG = "ProcMemInfo";

    public void startup(Context context) { }

    public synchronized void update(State state) {
        String loadAvgStr;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"), 1024);
            try {
                while ((loadAvgStr = reader.readLine()) != null) {
                    /* 0: label, 1: value, 2: human readable */
                    String[] split = loadAvgStr.split("\\s+");
                    if (split.length == 3) {
                        long value = 0;
                        try {
                            value = new Long(split[1]).longValue();
                            if ("kB".equals(split[2])) {
                                value = value * 1024;
                            }
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "", e);
                        }
                        if ("MemTotal:".equals(split[0])) {
                            memTotal = value;
                        } else if ("MemFree:".equals(split[0])) {
                            memFree = value;
                        } else if ("Buffers:".equals(split[0])) {
                            buffers = value;
                        } else if ("Cached:".equals(split[0])) {
                            cached = value;
                        } else if ("SwapTotal:".equals(split[0])) {
                            swapTotal = value;
                        } else if ("SwapFree:".equals(split[0])) {
                            swapFree = value;
                        }
                    }
                }
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Exception getting /proc/meminfo", e);
        }
    }

    public void destroy(Context context) { }

    public boolean runWhenInvisible() {
        return false;
    }

    public synchronized long getMemTotal() {
        return memTotal;
    }

    public synchronized long getMemFree() {
        return memFree;
    }

    public synchronized long getSwapTotal() {
        return swapTotal;
    }

    public synchronized long getSwapFree() {
        return swapFree;
    }

    public synchronized long getBuffers() {
        return buffers;
    }

    public synchronized long getCached() {
        return cached;
    }

    protected long memTotal;
    protected long memFree;
    protected long swapTotal;
    protected long swapFree;
    protected long buffers;
    protected long cached;
}
