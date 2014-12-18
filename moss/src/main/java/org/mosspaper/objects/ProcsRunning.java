package org.mosspaper.objects;

public class ProcsRunning extends AbsMossObject implements MossObject {

    /**
     * Number of processes in runnable state.
     */
    public ProcsRunning() { }

    public DataProvider getDataProvider() {
        return stat;
    }

    @Override
    public String toString() {
        return String.valueOf(stat.getProcsRunning());
    }

    private ProcList stat = ProcList.INSTANCE;
}

