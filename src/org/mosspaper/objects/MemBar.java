package org.mosspaper.objects;

import org.mosspaper.Env;

public class MemBar extends AbsBarObject implements MossObject {

    /**
     * Display memory used in a bar graph.
     */
    public MemBar() {
        super();
    }

    /**
     * Display memory used in a bar graph.
     *
     * @param hw comma delimited string of height and width
     */
    public MemBar(String hw) {
        super(hw);
    }

    public DataProvider getDataProvider() {
        return memInfo;
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        float perc = 0.0f;
        if (memInfo.getMemTotal() > 0) {
            perc = (memInfo.getMemTotal() - memInfo.getMemFree())
                 / (float)  memInfo.getMemTotal();
        }
        doDraw(env, perc);
    }

    public void postDraw(Env env) { }

    private ProcMemInfo memInfo = ProcMemInfo.INSTANCE;
}
