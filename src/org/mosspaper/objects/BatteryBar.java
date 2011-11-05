package org.mosspaper.objects;

import org.mosspaper.Env;
import org.mosspaper.Common;
import org.mosspaper.ParseException;
import org.mosspaper.util.Bar;

public class BatteryBar extends AbsBarObject  implements MossObject {

    /**
     * Display a bar of the current battery level.
     */
    public BatteryBar(String hw) throws ParseException {
        super(hw);
    }

    public DataProvider getDataProvider() {
        return battInfo;
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        doDraw(env, battInfo.getLevelFrac());
    }

    public void postDraw(Env env) { }

    private BatteryReceiver battInfo = BatteryReceiver.INSTANCE;
    private float barHeight;
    private float barWidth;
}
