package org.mosspaper.objects;

public class Battery extends AbsMossObject implements MossObject {

    /**
     * Display current battery status and level.
     */
    public Battery() { }

    public DataProvider getDataProvider() {
        return battInfo;
    }

    @Override
    public String toString() {
        return String.format("%s %.2f%%",
                battInfo.getStatusDesc(), battInfo.getLevelFrac() * 100.0f);
    }

    private BatteryReceiver battInfo = BatteryReceiver.INSTANCE;
}

