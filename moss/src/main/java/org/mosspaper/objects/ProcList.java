package org.mosspaper.objects;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.mosspaper.Common;
import org.mosspaper.DataService.State;
import org.mosspaper.util.RRDList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ProcList implements DataProvider {

    INSTANCE;

    public class Proc {

        public Proc clone() {
            Proc n = new Proc();

            n.pid = this.pid;
            n.packageName = this.packageName;
            n.name = this.name;
            n.state = this.state;

            n.cpu = this.cpu;

            n.userTime = this.userTime;
            n.kernelTime = this.kernelTime;

            n.mem = this.mem;
            n.memSize = this.memSize;
            n.oldMemSize = this.oldMemSize;

            n.cpuPerc = this.cpuPerc;
            n.memPerc = this.memPerc;
            return n;
        }

        String pid;
        String packageName;
        String name;
        String state;

        long cpu;

        long userTime;
        long kernelTime;

        long mem;
        long memSize;
        long oldMemSize;

        float cpuPerc;
        float memPerc;

        long timestamp;
    }

    class CpuInfo implements Graphable {
        @Override
        public CpuInfo clone() {
            CpuInfo info = new CpuInfo();
            info.cpuUsage = cpuUsage;
            info.utime = utime;
            info.stime = stime;
            info.cpuTotal = cpuTotal;
            info.memTotal = memTotal;
            info.processes = processes;
            info.procsRunning = procsRunning;
            return info;
        }

        public long getValue() {
            return (long) (cpuUsage * 100.0f);
        }

        float cpuUsage;

        long utime;
        long stime;

        long processes;
        long procsRunning;

        long cpuTotal;
        long memTotal;
    }

    ProcList() {
        statPattern = Pattern.compile(STAT_REGEX);
        statPidPattern = Pattern.compile(STAT_PID_REGEX);
        memPattern = Pattern.compile(MEM_REGEX);

        oldCpu = new CpuInfo();
        newCpu = new CpuInfo();
        timestamp = 0;
        whenInvisible = false;
    }


    public synchronized long getProcessCount() {
        if (null != newCpu) {
            return newCpu.processes;
        } else {
            return 0;
        }
    }

    public synchronized long getProcsRunning() {
        if (null != newCpu) {
            return newCpu.procsRunning;
        } else {
            return 0;
        }
    }

    public synchronized List<Proc> getProcesses() {
        return new ArrayList<Proc>(processes.values());
    }

    public synchronized List<Graphable> getCpuHistory() {
        this.whenInvisible = true;
        return new ArrayList<Graphable>(cpuHistory);
    }

    public synchronized float getCpuUsage() {
        return newCpu.cpuUsage;
    }

    public void startup(Context context) {
        pm = context.getPackageManager();
    }

    public synchronized void update(State state) {
        File proc = new File("/proc/");
        processCpuStats();

        if (!state.isVisible()) {
            wasVisible = false;
            return;
        }

        List<Thread> threads = new ArrayList<Thread>();
        for (final String pid : proc.list()) {
            if (!isNum(pid)) {
                continue;
            }
            Proc p = processes.get(pid);
            if (null == p) {
                p = new Proc();
                p.pid = pid;
                processCmdLine(p);
                processes.put(p.pid, p);
            }
            p.timestamp = timestamp;
            processPidStats(p);
            processPidMem(p);
        }

        /* Cleanup dead processes */
        for (Iterator<Proc> i = processes.values().iterator(); i.hasNext();) {
            Proc p = i.next();
            if (p.timestamp != timestamp) {
                i.remove();
            }
        }

        // this.procCopy = new ArrayList<Proc>(processes.size());
        for (Proc p : processes.values()) {
            if (wasVisible) {
                p.cpuPerc = 100.0f * (p.cpu / (float) (newCpu.cpuTotal - oldCpu.cpuTotal));
                p.memPerc = 100.0f * (p.memSize / (float) newCpu.memTotal);
            }
            // procCopy.add(p.clone());
        }

        timestamp++;
        wasVisible = true;
    }

    public void destroy(Context context) { }

    public boolean runWhenInvisible() {
        return whenInvisible;
    }

    private void processCpuStats() {
        String statStr;
        String filename = "/proc/stat";

        oldCpu = newCpu;
        newCpu.memTotal = 0;
        newCpu = new CpuInfo();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
            try {
                statStr = reader.readLine();

                Matcher m = statPattern.matcher(statStr);
                if (!m.matches()) {
                    Log.e(TAG, "Regex did not match on /proc/stat: " + statStr);
                } else {
                    newCpu.utime = Common.toLong(m.group(2)) + Common.toLong(m.group(3));
                    newCpu.stime = Common.toLong(m.group(4));
                    newCpu.cpuTotal = Common.toLong(m.group(2))
                        + Common.toLong(m.group(3))
                        + Common.toLong(m.group(4))
                        + Common.toLong(m.group(5))
                        + Common.toLong(m.group(6))
                        + Common.toLong(m.group(7))
                        + Common.toLong(m.group(8))
                        + Common.toLong(m.group(9))
                        + Common.toLong(m.group(10));

                    setCpuUsage();
                    cpuHistory.add(newCpu.clone());
                }

                /* Skip all lines ntil 'processes' */
                while ((statStr = reader.readLine()) != null) {
                    if (statStr.indexOf("processes") > -1) {
                        break;
                    }
                }

                String[] split = statStr.split("\\s+");
                if (split.length == 2) {
                    newCpu.processes = Common.toLong(split[1]);
                }

                statStr = reader.readLine();
                split = statStr.split("\\s+");
                if (split.length == 2) {
                    newCpu.procsRunning = Common.toLong(split[1]);
                }
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Exception getting /proc/\\d+/stat", e);
        }
    }

    private void processCmdLine(Proc p) {
        String filename = "/proc/" + p.pid + "/cmdline";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
            try {
                int c;
                StringBuffer cmdline = new StringBuffer("");
                while ((c = reader.read()) != -1) {
                    if (c < 32 || c >= 127) {
                        break;
                    }
                    cmdline.append((char) c);
                }
                p.packageName = cmdline.toString();
                p.name = findAppLabel(p.packageName);
            } finally {
                reader.close();
            }
        } catch (FileNotFoundException e) {
            processes.remove(p.pid);
        } catch (IOException e) {
            Log.e(TAG, "IO Exception getting /proc/\\d+/cmdline", e);
        }
    }

    private void processPidStats(Proc p) {
        String statStr;
        String filename = "/proc/" + p.pid + "/stat";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
            try {
                statStr = reader.readLine();

                Matcher m = statPidPattern.matcher(statStr);
                if (!m.matches()) {
                    Log.e(TAG, "Regex did not match on /proc/" + p.pid + "/stat: " + statStr);
                } else {
                    if (p.packageName == null || "".equals(p.packageName)) {
                        p.packageName = m.group(2);
                        p.name = findAppLabel(m.group(2));
                    }
                    p.state = m.group(3);
                    try {
                        long oldUserTime = p.userTime;
                        long oldKernelTime = p.kernelTime;

                        p.userTime = Common.toLong(m.group(14));
                        p.kernelTime = Common.toLong(m.group(15));
                        p.cpu = (p.userTime + p.kernelTime) - (oldUserTime + oldKernelTime);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Times did not match RE", e);
                    }
                }
            } finally {
                reader.close();
            }
        } catch (FileNotFoundException e) {
            processes.remove(p.pid);
        } catch (IOException e) {
            Log.e(TAG, "IO Exception getting /proc/\\d+/stat", e);
        }
    }

    private void processPidMem(Proc p) {
        String memStr;
        String filename = "/proc/" + p.pid + "/statm";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename), 64);
            try {
                memStr = reader.readLine();

                Matcher m = memPattern.matcher(memStr);
                if (!m.matches()) {
                    Log.e(TAG, "Regex did not match on /proc/" + p.pid + "/statm: " + memStr);
                } else {
                    p.oldMemSize = p.memSize;
                    p.memSize = Common.toLong(m.group(1));
                    newCpu.memTotal += p.memSize;
                }
            } finally {
                reader.close();
            }
        } catch (FileNotFoundException e) {
            processes.remove(p.pid);
        } catch (IOException e) {
            Log.e(TAG, "IO Exception getting /proc/\\d+/io", e);
        }
    }

    boolean isNum(String name) {
        try {
            new Integer(name);
        } catch (NumberFormatException _e) {
            return false;
        }
        return true;
    }

    String findAppLabel(String packageName) {
        if (null == packageLookup.get(packageName)) {
            String label = null;
            try {
                ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
                if (null != ai) {
                    label = pm.getApplicationLabel(ai).toString();
                }
            } catch (PackageManager.NameNotFoundException e) { /* not worried about it */ ; }
            if (null == label) {
                label = packageName;
            }
            packageLookup.put(packageName, label);
            return label;
        } else {
            return packageLookup.get(packageName);
        }
    }

    void setCpuUsage() {
        if (null != oldCpu && null != newCpu) {
            float delta = newCpu.cpuTotal - oldCpu.cpuTotal;
            if (delta > 0) {
                newCpu.cpuUsage = ((newCpu.utime) - (oldCpu.utime)) / delta;
            } else {
                newCpu.cpuUsage = 0.0f;
            }
        } else {
            newCpu.cpuUsage = 0.0f;
        }
    }

    private long timestamp;
    private CpuInfo oldCpu;
    private CpuInfo newCpu;
    private PackageManager pm;
    private boolean whenInvisible;
    boolean wasVisible;

    private Pattern statPattern;
    static final String STAT_REGEX =
            "^"
            + "(\\w+)\\s+" // cpu
            + "(\\d+)\\s+" // user mod
            + "(\\d+)\\s+" // user mode/niceval
            + "(\\d+)\\s+" // system mod
            + "(\\d+)\\s+" // idle
            + "(\\d+)\\s+" // iowait
            + "(\\d+)\\s+" // irq
            + "(\\d+)\\s+" // softirq
            + "(\\d+)\\s+" // steal
            + "(\\d+)\\s*.*"; // quest

    private Pattern memPattern;
    static final String MEM_REGEX =
            "^"
            + "(\\d+)\\s+" // size
            + "(\\d+)\\s+" // resident
            + "(\\d+)\\s+" // share
            + "(\\d+)\\s+" // text
            + "(\\d+)\\s+" // lib
            + "(\\d+)\\s+" // data
            + "(\\d+)\\s*$"; // dt

    private Pattern statPidPattern;
    static final String STAT_PID_REGEX =
            "^"
            + "(\\d+)\\s+"      /* pid */
            + "\\((.*?)\\)\\s+" /* name */
            + "(.*?)\\s+" /* state */
            + "(.*?)\\s+" /* ppid %d */
            + "(.*?)\\s+" /* pgrp %d */
            + "(.*?)\\s+" /* session %d */
            + "(.*?)\\s+" /* tty_nr %d */
            + "(.*?)\\s+" /* tpgid %d */
            + "(.*?)\\s+" /* flags %u */
            + "(.*?)\\s+" /* minflt %lu */
            + "(.*?)\\s+" /* cminflt %lu */
            + "(.*?)\\s+" /* majflt %lu */
            + "(.*?)\\s+" /* cmajflt %lu */
            + "(.*?)\\s+" /* utime %lu */
            + "(.*?)\\s+" /* stime %lu */
            + ".*$";

    static final String TAG = "ProcList";
    // static List<Proc> procCopy;
    static List<CpuInfo> cpuHistory;
    static Map<String, Proc> processes;
    static Map<String, String> packageLookup;
    static {
        cpuHistory = new RRDList<CpuInfo>(400);
        // procCopy = new ArrayList<Proc>();
        processes = new ConcurrentHashMap<String, Proc>();
        packageLookup = new ConcurrentHashMap<String, String>();
    }
}
