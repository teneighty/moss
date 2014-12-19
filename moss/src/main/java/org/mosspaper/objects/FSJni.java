package org.mosspaper.objects;

import android.content.Context;

import org.mosspaper.DataService.State;

import java.util.Map;
import java.util.HashMap;

public enum FSJni implements DataProvider {

    INSTANCE;

    public class StatFS {
        public String path;
        public long f_type;
        public long f_bsize;
        public long f_blocks;
        public long f_bfree;
        public long f_bavail;

        public long freeBytes;
        public long usedBytes;
        public long totalBytes;
    }

    public native void getFsInfo(String path, StatFS statFS);

    public synchronized void registerPath(String path) {
        if (null == stats) {
            stats = new HashMap<String, StatFS>();
        }
        if (null == stats.get(path)) {
            StatFS fs = new StatFS();
            fs.path = path;
            stats.put(path, fs);
        }
    }

    public void startup(Context context) { }

    public synchronized void update(State state) {
        for (StatFS fs : stats.values()) {
            getFsInfo(fs.path, fs);

            fs.freeBytes = fs.f_bsize * fs.f_bfree;
            fs.totalBytes = fs.f_bsize * fs.f_blocks;
            fs.usedBytes = fs.totalBytes - fs.freeBytes;
        }
    }

    public void destroy(Context context) { }

    public boolean runWhenInvisible() {
        return false;
    }

    public synchronized StatFS getStatFS(String path) {
        return stats.get(path);
    }

    private Map<String, StatFS> stats;

    static {
        System.loadLibrary("moss");
    }
}
