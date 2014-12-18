package org.mosspaper;

import org.mosspaper.objects.DataProvider;
import org.mosspaper.objects.UpdateManager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class DataService extends Service {

    public class State {
        public boolean isVisible() {
            return visible;
        }
        boolean visible;
    }

    class DataBinder extends Binder {
        DataService getService() {
            return DataService.this;
        }
    }

    @Override
    public void onCreate() {
        handler.post(handlerCallback);
        visible = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(handlerCallback);
        destroyProviders();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public synchronized void setDataProviders(Map<DataProvider, UpdateManager> providers) {
        destroyProviders();
        this.dataProviders = providers;
        startupProviders();
    }

    public synchronized void setPaperVisible(boolean visible) {
        this.visible = visible;
    }

    void startupProviders() {
        if (null == dataProviders) {
            return;
        }
        for (DataProvider d : dataProviders.keySet()) {
            d.startup(this);
        }
    }

    void destroyProviders() {
        if (null == dataProviders) {
            return;
        }
        for (DataProvider d : dataProviders.keySet()) {
            d.destroy(this);
        }
    }

    void updateProviders() {
        Long nextRun = 250L;
        if (null != dataProviders) {
            final State state = new State();
            state.visible = visible;

            nextRun = null;

            List<Thread> threads = new ArrayList<Thread>();
            for (final UpdateManager m : dataProviders.values()) {
                /* determine when to update again */
                if (null == nextRun || nextRun > m.getInterval()) {
                    nextRun = m.getInterval();
                }
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        m.update(state);
                    }
                });
                t.start();
                threads.add(t);
            }
            try {
                for (Thread t : threads) {
                    t.join();
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "", e);
            }
        }
        if (nextRun == null) {
            nextRun = 1L;
        }
        handler.postDelayed(handlerCallback, nextRun);
    }

    private final Runnable handlerCallback = new Runnable() {
        public void run() {
            new Thread(new Runnable() {
                public void run() {
                    updateProviders();
                }
            }).start();
        }
    };

    private Map<DataProvider, UpdateManager> dataProviders;
    private Boolean visible;

    private final IBinder mBinder = new DataBinder();
    private final Handler handler = new Handler();

    static final int DEFAULT_INTERVAL = 1000;
    static final String TAG = "DataService";
}
