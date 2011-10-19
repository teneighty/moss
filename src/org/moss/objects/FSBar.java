package org.moss.objects;

import org.moss.Env;
import org.moss.Common;
import org.moss.objects.FSJni.StatFS;

public class FSBar implements MossObject {

    /**
     * Display current file system usage in a usage bar.
     *
     * @param mountPoint Mount point such as /sdcard, /system or /data
     */

    public FSBar(String mountPoint) {
        fs.registerPath(mountPoint);
        this.mountPoint = mountPoint;
    }

    public DataProvider getDataProvider() {
        return fs;
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        StatFS stat = fs.getStatFS(mountPoint);
        float frac = stat.usedBytes / (float) stat.totalBytes;
        Common.drawBar(env, frac);
    }

    public void postDraw(Env env) { }

    private FSJni fs = FSJni.INSTANCE;
    private String mountPoint;
}
