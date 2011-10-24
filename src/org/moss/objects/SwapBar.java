package org.moss.objects;

import org.moss.Env;
import org.moss.util.Bar;

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
        Bar b = new Bar();
        b.drawBar(env, perc);
    }

    public void postDraw(Env env) { }

    private ProcMemInfo memInfo = ProcMemInfo.INSTANCE;
}
