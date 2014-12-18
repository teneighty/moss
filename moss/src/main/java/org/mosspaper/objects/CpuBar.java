package org.mosspaper.objects;

import org.mosspaper.Env;
import org.mosspaper.util.Bar;

public class CpuBar extends AbsBarObject implements MossObject {

    /**
     * Display the percentage of cpu usage in a bar.
     */
    public CpuBar() { 
        super();
    }

    /**
     * Display the percentage of cpu usage in a bar.
     */
    public CpuBar(String hw) { 
        super(hw);
    }

    public DataProvider getDataProvider() {
        return cpuInfo;
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        doDraw(env, cpuInfo.getCpuUsage());
    }

    public void postDraw(Env env) { }

    private ProcList cpuInfo = ProcList.INSTANCE;
}
