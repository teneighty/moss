package org.mosspaper.objects;

import org.mosspaper.Common;
import org.mosspaper.objects.FSJni.StatFS;

public class FSUsed extends AbsMossObject implements MossObject {

    /**
     * Display the amount of disk usage.
     *
     * @param mountPoint Mount point such as /sdcard, /system or /data
     */
    public FSUsed(String mountPoint) {
        fs.registerPath(mountPoint);
        this.mountPoint = mountPoint;
    }

    public DataProvider getDataProvider() {
        return fs;
    }

    @Override
    public String toString() {
        StatFS stat = fs.getStatFS(mountPoint);
        return Common.humanReadble(stat.usedBytes);
    }

    private FSJni fs = FSJni.INSTANCE;
    private String mountPoint;
}
