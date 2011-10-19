package org.moss.objects;

import org.moss.Common;
import org.moss.objects.FSJni.StatFS;

public class FSFree extends AbsMossObject implements MossObject {

    /**
     * Display available diskspace.
     *
     * @param mountPoint Mount point such as /sdcard, /system or /data
     */
    public FSFree(String mountPoint) {
        fs.registerPath(mountPoint);
        this.mountPoint = mountPoint;
    }

    public DataProvider getDataProvider() {
        return fs;
    }

    @Override
    public String toString() {
        StatFS stat = fs.getStatFS(mountPoint);
        return Common.humanReadble(stat.freeBytes);
    }

    private FSJni fs = FSJni.INSTANCE;
    private String mountPoint;
}
