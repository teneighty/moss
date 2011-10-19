package org.moss.objects;

import org.moss.Common;
import org.moss.objects.FSJni.StatFS;

public class FSSize extends AbsMossObject implements MossObject {

    /**
     * Display disk size.
     *
     * @param mountPoint Mount point such as /sdcard, /system or /data
     */
    public FSSize(String mountPoint) {
        fs.registerPath(mountPoint);
        this.mountPoint = mountPoint;
    }

    public DataProvider getDataProvider() {
        return fs;
    }

    @Override
    public String toString() {
        StatFS stat = fs.getStatFS(mountPoint);
        return Common.humanReadble(stat.totalBytes);
    }

    private FSJni fs = FSJni.INSTANCE;
    private String mountPoint;
}
