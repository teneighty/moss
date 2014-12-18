package org.mosspaper.objects;

import org.mosspaper.Common;

public class SwapMax extends AbsMossObject implements MossObject {

    /**
     * Display the maximum swap memory available.
     */
    public SwapMax() {
        this.memInfo = ProcMemInfo.INSTANCE;
    }

    public DataProvider getDataProvider() {
        return memInfo;
    }

    @Override
    public String toString() {
        return Common.humanReadble(memInfo.getSwapTotal());
    }

    private ProcMemInfo memInfo;
}
