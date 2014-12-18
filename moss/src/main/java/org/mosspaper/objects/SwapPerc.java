package org.mosspaper.objects;

public class SwapPerc extends AbsMossObject implements MossObject {

    /**
     * Display the percentage of swap memory being used.
     */
    public SwapPerc() {
        this.memInfo = ProcMemInfo.INSTANCE;
    }

    public DataProvider getDataProvider() {
        return memInfo;
    }

    @Override
    public String toString() {
        double perc = 0.0;
        if (memInfo.getSwapTotal() > 0) {
            perc = (double) (memInfo.getSwapTotal() - memInfo.getSwapFree())
                 / (double) memInfo.getSwapTotal();
        }
        return String.format("%.2f", perc * 100.0);
    }

    private ProcMemInfo memInfo;
}
