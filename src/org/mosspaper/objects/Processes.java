package org.mosspaper.objects;

public class Processes extends AbsMossObject implements MossObject {

    /**
     * Display the total number of forks since boot.
     */
    public Processes() { }

    public DataProvider getDataProvider() {
        return stat;
    }

    @Override
    public String toString() {
        return String.valueOf(stat.getProcessCount());
    }

    private ProcList stat = ProcList.INSTANCE;
}

