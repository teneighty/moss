package org.mosspaper.objects;

import org.mosspaper.objects.FSJni.StatFS;

public class FSUsedPerc extends AbsMossObject implements MossObject {

    /**
     * Display the amount of disk usage.
     *
     * @param mountPoint Mount point such as /sdcard, /system or /data
     */
    public FSUsedPerc(String mountPoint) {
        fs.registerPath(mountPoint);
        this.mountPoint = mountPoint;
    }

    public DataProvider getDataProvider() {
        return fs;
    }

    @Override
    public String toString() {
        StatFS stat = fs.getStatFS(mountPoint);
        float frac = 100.0f * (stat.usedBytes / (float) stat.totalBytes);

        return String.format("%.2f", frac);
    }

    private FSJni fs = FSJni.INSTANCE;
    private String mountPoint;
}
