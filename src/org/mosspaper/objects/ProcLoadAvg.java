package org.mosspaper.objects;

import android.content.Context;
import android.util.Log;

import org.mosspaper.DataService.State;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ProcLoadAvg implements DataProvider {

    INSTANCE;

    static final String TAG = "ProcLoadAvg";

    private String[] loadAvg;
    private Pattern regex;

    ProcLoadAvg() {
        final String LOAD_AVG_REGEX = "^(.+?) (.+?) (.+?) .*$";
        this.loadAvg = new String[] {"", "", ""};
        this.regex = Pattern.compile(LOAD_AVG_REGEX);;
    }

    public void startup(Context context) { }

    public synchronized void update(State state) {
        String loadAvgStr;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/loadavg"), 256);
            try {
                loadAvgStr = reader.readLine();
            } finally {
                reader.close();
            }

            Matcher m = regex.matcher(loadAvgStr);

            if (!m.matches()) {
                Log.e(TAG, "Regex did not match on /proc/version: " + loadAvgStr);
            } else {
                loadAvg[0] = m.group(1);
                loadAvg[1] = m.group(2);
                loadAvg[2] = m.group(3);

                return;
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Exception getting /proc/loadavg", e);
        }
    }

    public void destroy(Context context) { }

    public boolean runWhenInvisible() {
        return false;
    }

    public synchronized String[] getLoadAvg() {
        return loadAvg;
    }
}
