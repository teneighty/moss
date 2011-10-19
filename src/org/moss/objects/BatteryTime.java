package org.moss.objects;

import org.moss.Common;

public class BatteryTime extends AbsMossObject implements MossObject {

    /**
     * Display the amount of time left on battery.
     */
    public BatteryTime() { }

    public DataProvider getDataProvider() {
        return battInfo;
    }

    @Override
    public String toString() {
        if (battInfo.getTimeRemaining() < 0) {
            return "Unknown";
        } else {
            return Common.formatSeconds(battInfo.getTimeRemaining());
        }
    }

    private BatteryReceiver battInfo = BatteryReceiver.INSTANCE;
}

