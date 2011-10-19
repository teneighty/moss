package org.moss.objects;

import org.moss.Common;

public class Mem extends AbsMossObject implements MossObject {

    /**
     * Display memory used.
     */
    public Mem() {
    }

    public DataProvider getDataProvider() {
        return memInfo;
    }

    @Override
    public String toString() {
        return Common.humanReadble(memInfo.getMemTotal() - memInfo.getMemFree());
    }

    private ProcMemInfo memInfo = ProcMemInfo.INSTANCE;
}

