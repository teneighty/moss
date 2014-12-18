package org.mosspaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class WallPaper extends WallpaperService {

    static final String SHARED_PREFS_NAME = "MossSettings";
    static final String TAG = "WallPaper";

    static final int DEFAULT_INTERVAL = 1000;
    static final int HANDLER_INTERVAL = 200;

    private MossEngine mossEngine;
    private final IBinder mBinder = new PaperBinder();

    class PaperBinder extends Binder {
        WallPaper getService() {
            return WallPaper.this;
        }
    }

    public WallPaper() { }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        this.mossEngine = new MossEngine();
        return mossEngine;
    }

    public MossEngine getEngine() {
        return mossEngine;
    }

    class MossEngine extends Engine
        implements SharedPreferences.OnSharedPreferenceChangeListener {

        private final Handler mHandler = new Handler();
        private float mOffset;
        private SharedPreferences prefs;
        private Env.Current single = Env.Current.INSTANCE;
        private DataService dataService;
        private boolean isBound = false;
        private boolean isVisible = false;

        private final Runnable mDrawMoss = new Runnable() {
            public void run() {
                drawFrame();
            }
        };

        /* Reload the configuration once external media becomes available */
        private BroadcastReceiver mSdReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                reloadConfig();
            }
        }; 

        private ServiceConnection sconn = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                dataService = ((DataService.DataBinder) service).getService();
                if (null != single.env) {
                    dataService.setDataProviders(single.env.getDataProviders());
                }
            }

            public void onServiceDisconnected(ComponentName className) {
                dataService = null;
            }
        };

        MossEngine() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter.addDataScheme("file");
            registerReceiver(mSdReceiver, new IntentFilter(filter));

            mOffset = 0.5f;
            prefs = WallPaper.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
            prefs.registerOnSharedPreferenceChangeListener(this);

            /* Maybe this should be in its own thread */
            /* Load config */
            onSharedPreferenceChanged(prefs, null);

            /* Start the data service */
            doBindService();
        }

        void doBindService() {
            if (!isBound) {
                bindService(new Intent(WallPaper.this,
                        DataService.class), sconn, Context.BIND_AUTO_CREATE);
                isBound = true;
            }
        }

        void doUnbindService() {
            if (isBound) {
                unbindService(sconn);
                isBound = false;
            }
        }

        void reloadConfig() {
            Env.load(WallPaper.this, mHandler);

            /* Update data providers and restart service */
            if (null != single.env.getDataProviders()) {
                doBindService();
                if (null != dataService) {
                    dataService.setDataProviders(single.env.getDataProviders());
                }
            }
        }

        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key == null
                    || "config_file".equals(key)
                    || "sample_config_file".equals(key)) {
                reloadConfig();
            } else {
                if (single.env != null) {
                    single.env.loadPrefs(WallPaper.this, prefs);
                    if ("update_interval".equals(key)) {
                        single.env.buildDataProviders();
                        if (dataService != null) {
                            dataService.setDataProviders(single.env.getDataProviders());
                        }
                    }
                }
            }
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(false);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            doUnbindService();
            mHandler.removeCallbacks(mDrawMoss);
            unregisterReceiver(mSdReceiver);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.isVisible = visible;
            if (dataService != null) {
                dataService.setPaperVisible(isVisible);
            }
            if (isVisible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDrawMoss);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            single.env.setPaperHeight(height);
            single.env.setPaperWidth(width);

            /* Draw frame twice since this will be the first time drawing the
             * new frame.
             */
            drawFrame();
            drawFrame();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.isVisible = false;
            if (dataService != null) {
                dataService.setPaperVisible(isVisible);
            }
            mHandler.removeCallbacks(mDrawMoss);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xStep, float yStep, int xPixels, int yPixels) {
            this.mOffset = xOffset;
        }

        void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();
            final Rect frame = holder.getSurfaceFrame();
            final int width = frame.width();
            final int height = frame.height();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    if (single.env.getMaxX() <= 0) {
                        /** getMaxX <= 0 so this is the first time drawing.
                         * Moss doesn't know how wide this configuration is
                         * until it has drawn it once. Second time around it
                         * can calculate the proper startx and starty
                         */
                        single.env.draw(c);
                    }
                    single.env.draw(c);
                }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }

            mHandler.removeCallbacks(mDrawMoss);
            if (this.isVisible) {
                if (single.env != null) {
                    mHandler.postDelayed(mDrawMoss, single.env.getUpdateInterval());
                } else {
                    mHandler.postDelayed(mDrawMoss, DEFAULT_INTERVAL);
                }
                if (null != dataService) {
                    dataService.setPaperVisible(isVisible);
                }
            }
        }
    }
}
