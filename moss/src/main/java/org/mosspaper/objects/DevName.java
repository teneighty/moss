package org.mosspaper.objects;

import org.mosspaper.Common;

public class DevName extends AbsMossObject implements MossObject {

    /**
     * Display the device name
     *
     * @param device network device such as eth0 or wlan0
     */
    public DevName(String device) {
        this.device = device;
        netDevInfo.registerDevice(device);
    }

    public DataProvider getDataProvider() {
        return netDevInfo;
    }

    @Override
    public String toString() {
        return netDevInfo.getDevName(device);
    }

    private String device;
    private ProcNetDev netDevInfo = ProcNetDev.INSTANCE;
}

