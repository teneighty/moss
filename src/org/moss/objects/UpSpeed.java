package org.moss.objects;

import org.moss.Common;

public class UpSpeed extends AbsMossObject implements MossObject {

    /**
     * Display the upload speed for a network interface.
     *
     * @param device network device such as eth0 or wlan0
     */
    public UpSpeed(String device) {
        this.device = device;
    }

    public DataProvider getDataProvider() {
        return netDevInfo;
    }

    @Override
    public String toString() {
        return Common.humanReadble(netDevInfo.getUpSpeed(device));
    }

    private String device;
    private ProcNetDev netDevInfo = ProcNetDev.INSTANCE;
}

