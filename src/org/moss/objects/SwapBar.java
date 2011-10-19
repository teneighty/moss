package org.moss.objects;

import org.moss.Common;
import org.moss.Env;

public class SwapBar implements MossObject {

    /**
     * Display swap memory used in a bar graph.
     */
    public SwapBar() { }

    public DataProvider getDataProvider() {
        return memInfo;
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        float perc = 0.0f;
        if (memInfo.getSwapTotal() > 0) {
            perc = (memInfo.getSwapTotal() - memInfo.getSwapFree())
                 / (float) memInfo.getSwapTotal();
        }
        Common.drawBar(env, perc);
    }

    public void postDraw(Env env) { }

    private ProcMemInfo memInfo = ProcMemInfo.INSTANCE;
}
