package org.mosspaper.objects;

import org.mosspaper.Env;
import org.mosspaper.util.Bar;

public class SwapBar extends AbsBarObject implements MossObject {

    /**
     * Display swap memory used in a bar graph.
     */
    public SwapBar() { }

    /**
     * Display swap used in a bar graph.
     *
     * @param hw comma delimited string of height and width
     */
    public SwapBar(String hw) {
        super(hw);
    }

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
        doDraw(env, perc);
    }

    public void postDraw(Env env) { }

    private ProcMemInfo memInfo = ProcMemInfo.INSTANCE;
}
