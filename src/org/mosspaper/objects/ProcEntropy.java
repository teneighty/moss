package org.mosspaper.objects;

import android.content.Context;
import android.util.Log;

import org.mosspaper.DataService.State;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public enum ProcEntropy implements DataProvider {

    INSTANCE;

    ProcEntropy() { }

    public void startup(Context context) { }

    private long slurp(String filePath) throws IOException {
        StringBuffer buf = new StringBuffer("");
        BufferedReader reader =
            new BufferedReader(new FileReader(filePath), INIT_BUFFER);
        try {
            String str;
            while ((str = reader.readLine()) != null) {
                buf.append(str).append("\n");
            }
        } finally {
            reader.close();
        }
        return Long.parseLong(buf.toString().trim());
    }

    public synchronized void update(State state) {
        String devStr;
        try {
            available = slurp("/proc/sys/kernel/random/entropy_avail");
            poolSize = slurp("/proc/sys/kernel/random/poolsize");
        } catch (Exception e) {
            Log.e(TAG, "IO Exception getting entropy", e);
        }
    }

    public synchronized long getAvailable() {
        return available;
    }

    public synchronized long getPoolSize() {
        return poolSize;
    }

    public synchronized float getPerc() {
        if (poolSize == 0) {
            return 0.0f;
        } else {
            return available / (float) poolSize;
        }
    }

    public void destroy(Context context) { }

    public boolean runWhenInvisible() {
        return false;
    }

    static long available = 0;
    static long poolSize = 0;

    static final int INIT_BUFFER = 64;
    static final String TAG = "ProcEntropy";
}
