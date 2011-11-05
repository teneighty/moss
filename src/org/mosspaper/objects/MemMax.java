package org.mosspaper.objects;

import org.mosspaper.Common;

public class MemMax extends AbsMossObject implements MossObject {

    /**
     *  Display the maximum available memory.
     */
    public MemMax() {
    }

    public DataProvider getDataProvider() {
        return memInfo;
    }

    @Override
    public String toString() {
        return Common.humanReadble(memInfo.getMemTotal());
    }

    private ProcMemInfo memInfo = ProcMemInfo.INSTANCE;
}

