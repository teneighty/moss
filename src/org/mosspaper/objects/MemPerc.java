package org.mosspaper.objects;

public class MemPerc extends AbsMossObject implements MossObject {

    /**
     * Display the percentage of memory being used.
     */
    public MemPerc() { }

    public DataProvider getDataProvider() {
        return memInfo;
    }

    @Override
    public String toString() {
        double perc = 0.0;
        if (memInfo.getMemTotal() > 0) {
            perc = (double) (memInfo.getMemTotal() - memInfo.getMemFree())
                 / (double)  memInfo.getMemTotal();
        }
        return String.valueOf(Math.ceil(perc * 100.0));
    }

    private ProcMemInfo memInfo = ProcMemInfo.INSTANCE;
}
