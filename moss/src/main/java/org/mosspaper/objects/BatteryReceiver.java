package org.mosspaper.objects;

import org.mosspaper.DataService.State;
import org.mosspaper.util.RRDList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import java.util.List;
import java.util.Iterator;

public enum BatteryReceiver implements DataProvider {
    INSTANCE;

    class BattInfo {
        int level;
        long sysTime;
        float lossPerMilli;
    }

    BatteryReceiver() {
        level = 0;
        statusDesc = "";
        battInfo = new RRDList<BattInfo>(MAX_HISTORY);
    }

    public void startup(Context context) {
        context.registerReceiver(this.receiver,
            new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void update(State state) { }

    public void destroy(Context context) {
        try {
            context.unregisterReceiver(this.receiver);
        } catch (Exception e) {
            Log.e(TAG, "unregisterReceiver", e);
        }
    }

    public boolean runWhenInvisible() {
        return false;
    }

    public synchronized float getLevelFrac() {
        if (scale == 0) {
            return 0.0f;
        } else {
            return level / (float) scale;
        }
    }

    public synchronized int getLevel() {
        return level;
    }

    public synchronized int getScale() {
        return scale;
    }

    public synchronized int getTemp() {
        return temp;
    }

    public synchronized int getVoltage() {
        return voltage;
    }

    public synchronized long getTimeRemaining() {
        return timeRemaining;
    }

    public synchronized String getStatusDesc() {
        return statusDesc;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (BatteryReceiver.this) {
                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                switch (status) {
                // case BatteryManager.BATTERY_PLUGGED_AC:
                // case BatteryManager.BATTERY_PLUGGED_USB:
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    statusDesc = "Charging";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    statusDesc = "Discharging";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    statusDesc = "Full";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    statusDesc = "Not Charging";
                    break;
                default:
                    statusDesc = "Unknown";
                }

                if (true) {
                    BattInfo info = new BattInfo();
                    info.level = level;
                    info.sysTime = System.currentTimeMillis();

                    if (battInfo.size() > 0) {
                        BattInfo prev = battInfo.get(0);
                        info.lossPerMilli = (info.level - prev.level) / (float) (info.sysTime - prev.sysTime);
                    }
                    battInfo.add(info);
                }

                /* calc avg loss of level per milli */
                float lossAvgPerMilli = 0.0f;
                for (Iterator<BattInfo> i = battInfo.iterator(); i.hasNext();) {
                    BattInfo info = i.next();
                    lossAvgPerMilli += info.lossPerMilli;
                }
                lossAvgPerMilli /= battInfo.size();
                if (lossAvgPerMilli <= 0.0) {
                    timeRemaining = -1;
                } else {
                    timeRemaining = (long) (level / (lossAvgPerMilli * 1000.0));
                }
            }
        }
    };

    private List<BattInfo> battInfo;
    private int level;
    private int scale;
    private int temp;
    private int voltage;
    private int status;
    private long timeRemaining;
    private String statusDesc;

    static final int MAX_HISTORY = 10;
    static final String TAG = "BatteryReceiver";
}
