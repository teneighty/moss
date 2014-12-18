package org.mosspaper.objects;

import org.mosspaper.Env;
import org.mosspaper.objects.FSJni.StatFS;

public class FSBar extends AbsBarObject implements MossObject {

    /**
     * Display current file system usage in a usage bar.
     *
     * @param mountPoint Mount point such as /sdcard, /system or /data
     */

    public FSBar(String mountPoint) {
        super();
        fs.registerPath(mountPoint);
        this.mountPoint = mountPoint;
    }

    public FSBar(String hw, String mountPoint) {
        super(hw);
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

        doDraw(env, frac);
    }

    public void postDraw(Env env) { }

    private FSJni fs = FSJni.INSTANCE;
    private String mountPoint;
}
