package org.moss.objects;

import org.moss.Common;

public class Swap extends AbsMossObject implements MossObject {

    /**
     * Display the amount of swap memory being used.
     */
    public Swap() { }

    public DataProvider getDataProvider() {
        return memInfo;
    }

    @Override
    public String toString() {
        return Common.humanReadble(memInfo.getSwapTotal() - memInfo.getSwapFree());
    }

    private ProcMemInfo memInfo = ProcMemInfo.INSTANCE;
}
