package org.mosspaper.objects;

import android.content.Context;
import android.util.Log;

import org.mosspaper.DataService.State;
import org.mosspaper.ParseException;

import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CpuFreqProvider implements DataProvider {

    INSTANCE;

    static final String TAG = "CpuFreqProvider";
    static final String CPUFREQ_PREFIX = "/sys/devices/system/cpu";
    static final String CPUFREQ_POSTFIX = "cpufreq/scaling_cur_freq";

    class Cpu {
        long freq;
    }

    private Map<Integer, Cpu> cpus;

    public void registerCpu(int cpuNum) throws ParseException {
        int adjNum = cpuNum - 1;
        if (cpus == null) {
            cpus = new HashMap<Integer, Cpu>();
        }

        java.io.File file = new java.io.File(makeFilename(adjNum));
        if (file.exists()) {
            if (null == cpus.get(adjNum)) {
                cpus.put(adjNum, new Cpu());
            }
        } else {
            throw new ParseException("Invalid CPU number " + cpuNum);
        }
    }

    public void startup(Context context) { }

    public synchronized void update(State state) {
        for (Map.Entry <Integer, Cpu> entry : cpus.entrySet()) {

            String freqStr;
            try {
                BufferedReader reader = 
                    new BufferedReader(
                            new FileReader(makeFilename(entry.getKey())), 32);
                try {
                    entry.getValue().freq = Long.parseLong(reader.readLine());
                } catch (NumberFormatException ne) {
                    Log.e(TAG, "", ne);
                } finally {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "IO Exception getting cpu freq", e);
            }
        }
    }

    public void destroy(Context context) { }

    public boolean runWhenInvisible() {
        return false;
    }

    private String makeFilename(int cpuNum) {
        return String.format("%s/cpu%d/%s", CPUFREQ_PREFIX, cpuNum, CPUFREQ_POSTFIX);
    }

    public synchronized float getFreq(int cpuNum) {
        cpuNum = cpuNum - 1;
        Cpu c = cpus.get(cpuNum);
        if (c != null) {
            return c.freq / 1000.0f;
        } else {
            return 0.0f;
        }
    }
}
