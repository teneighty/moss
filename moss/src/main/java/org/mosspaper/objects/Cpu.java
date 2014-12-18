package org.mosspaper.objects;

public class Cpu extends AbsMossObject implements MossObject {

    /**
     * Display the percentage of cpu usage.
     */
    public Cpu() { }

    public DataProvider getDataProvider() {
        return cpuInfo;
    }

    @Override
    public String toString() {
        return String.format("%.0f", 100.0f * cpuInfo.getCpuUsage());
    }

    private ProcList cpuInfo = ProcList.INSTANCE;
}

