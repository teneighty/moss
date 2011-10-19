package org.moss.objects;

import org.moss.Env;
import org.moss.Common;
import org.moss.ParseException;

public class BatteryBar implements MossObject {

    /**
     * Display a bar of the current battery level.
     */
    public BatteryBar(String heightWidth) throws ParseException {
        String[] hw = heightWidth.split(",");

        try {
            this.barHeight = Common.toFloat(hw[0]);
            this.barWidth = Common.toFloat(hw[1]);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid heightWidth");
        }
    }

    public DataProvider getDataProvider() {
        return battInfo;
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        Common.drawBar(env, battInfo.getLevelFrac(), barHeight, barWidth);
    }

    public void postDraw(Env env) { }

    private BatteryReceiver battInfo = BatteryReceiver.INSTANCE;
    private float barHeight;
    private float barWidth;
}
