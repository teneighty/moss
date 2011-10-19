package org.moss.objects;

import org.moss.Env;
import org.moss.Common;

public class CpuBar implements MossObject {

    /**
     * Display the percentage of cpu usage in a bar.
     */
    public CpuBar() { }

    public DataProvider getDataProvider() {
        return cpuInfo;
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        Common.drawBar(env, cpuInfo.getCpuUsage());
    }

    public void postDraw(Env env) { }

    private ProcList cpuInfo = ProcList.INSTANCE;
}
