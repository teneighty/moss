package org.mosspaper.objects;

import org.mosspaper.Env;

public class CpuGraph extends AbsGraphObject implements MossObject {

    /**
     * Display a historical graph of cpu usage.
     *
     * @param color1 left most color of graph
     * @param color2 right most color of graph
     */
    public CpuGraph(String color1, String color2) {
        super(color1, color2);
        this.scale = 100;
    }

    /**
     * Display a historical graph of cpu usage.
     *
     * @param hw height,width of the graph such as 32,120
     * @param color1 left most color of graph
     * @param color2 right most color of graph
     */
    public CpuGraph(String hw, String color1, String color2) {
        super(hw, color1, color2);
    }

    public DataProvider getDataProvider() {
        return cpuInfo;
    }

    public void preDraw(Env env) {
        if (null == history) {
            history = cpuInfo.getCpuHistory();
        }
    }

    public void postDraw(Env env) {
        history = null;
    }

    private ProcList cpuInfo = ProcList.INSTANCE;
}

