package org.mosspaper.objects;

public class BatteryPercent extends AbsMossObject implements MossObject {

    /**
     * Display current battery level.
     */
    public BatteryPercent() { }

    public DataProvider getDataProvider() {
        return battInfo;
    }

    @Override
    public String toString() {
        return String.format("%.2f%%", battInfo.getLevelFrac() * 100.0f);
    }

    private BatteryReceiver battInfo = BatteryReceiver.INSTANCE;
}

