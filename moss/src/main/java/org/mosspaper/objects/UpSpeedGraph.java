package org.mosspaper.objects;

import org.mosspaper.Env;

public class UpSpeedGraph extends AbsGraphObject implements MossObject {

    /**
     * Display a historical graph of upload speeds for a device.
     *
     * @param device network device such as eth0 or wlan0
     * @param hw height,width of the graph
     * @param color1 left most color of graph
     * @param color2 right most color of graph
     */
    public UpSpeedGraph(String device, String hw, String color1, String color2) {
        super(hw, color1, color2);
        this.device = device;
        this.scale = 1024 * 10;
        netDevInfo.registerDevice(device, Math.round(width));
    }

    public DataProvider getDataProvider() {
        return netDevInfo;
    }

    public void preDraw(Env env) {
        if (null == history) {
            history = netDevInfo.getUpHistory(device);
            if (null == history) {
                return;
            }
            int i = 0;
        }
    }

    public void postDraw(Env env) {
        history = null;
    }

    private String device;
    private ProcNetDev netDevInfo = ProcNetDev.INSTANCE;
}

