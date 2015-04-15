package org.mosspaper.objects;

import android.content.Context;
import android.util.Log;

import org.mosspaper.Common;
import org.mosspaper.DataService.State;
import org.mosspaper.util.RRDList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ProcNetDev implements DataProvider {

    INSTANCE;

    class Device {
        String deviceName;

        RRDList<DeviceStat> recvHistory;
        RRDList<DeviceStat> transHistory;

        long recvSpeed;
        long transSpeed;

        long totalDown;
        long totalUp;

        long recvLast;
        long transLast;
        long timestamp;
    }

    class DeviceStat implements Graphable {
        public long getValue() {
            return bytes;
        }
        long bytes;
    }

    ProcNetDev() {
        netDevPattern = Pattern.compile(PROC_NET_REGEX);
        devices = new HashMap<String, Device>();
    }

    public synchronized void registerDevice(String deviceName) {
        if (null == devices.get(deviceName)) {
            Device device = new Device();
            device.deviceName = deviceName;
            device.recvHistory = new RRDList<DeviceStat>(MAX_HISTORY);
            device.transHistory = new RRDList<DeviceStat>(MAX_HISTORY);
            devices.put(device.deviceName, device);
        }
    }

    public synchronized void registerDevice(String deviceName, int width) {
        registerDevice(deviceName);
        Device device = devices.get(deviceName);
        if (null != device) {
            device.recvHistory.setMaxCapacity(width);
            device.transHistory.setMaxCapacity(width);
        }
    }

    public void startup(Context context) { }

    public synchronized void update(State state) {
        String devStr;
        try {
            List<Device> genericDevs = new ArrayList<Device>();
            BufferedReader reader = new BufferedReader(new FileReader("/proc/net/dev"), INIT_BUFFER);
            long ts = System.currentTimeMillis();
            try {
                /* Skip first two lines */
                reader.readLine();
                reader.readLine();
                while ((devStr = reader.readLine()) != null) {
                    Matcher m = netDevPattern.matcher(devStr);
                    if (!m.matches()) {
                        Log.e(TAG, "Regex did not match on /proc/net/dev");
                    } else {
                        Device device = devices.get(m.group(1));
                        if (device == null) {
                            registerDevice(m.group(1));
                            device = devices.get(m.group(1));
                        }
                        if (null == device) {
                            continue;
                        }
                        device.totalDown = Common.toLong(m.group(2));
                        device.totalUp = Common.toLong(m.group(10));
                        if (device.totalDown > 0 || device.totalUp > 0) {
                            // Ignore loopback for generic devices
                            if (!"lo".equals(device.deviceName)) {
                                genericDevs.add(device);
                            }
                        }

                        if (true) {
                            DeviceStat stat = new DeviceStat();
                            device.recvSpeed = stat.bytes = device.totalDown - device.recvLast;
                            device.recvHistory.add(stat);
                        }
                        if (true) {
                            DeviceStat stat = new DeviceStat();
                            device.transSpeed = stat.bytes = device.totalUp - device.transLast;
                            device.transHistory.add(stat);
                        }

                        device.recvLast = device.totalDown;
                        device.transLast = device.totalUp;
                        device.timestamp = ts;
                    }
                }
                Collections.sort(genericDevs, new Comparator<Device>() {
                    public int compare(Device d1, Device d2) {
                        return (int) ((d2.totalDown + d2.totalUp) - (d1.totalDown + d1.totalUp));
                    }
                });
                int i = 0;
                for (Device d : genericDevs) {
                    devices.put("dev" + i, d);
                    i++;
                }
            } finally {
                reader.close();
            }
            /* If the device was removed, example wifi is turned off */
            for (Device d : devices.values()) {
                if (ts == d.timestamp) {
                    continue;
                }
                DeviceStat zero = new DeviceStat();
                zero.bytes = 0;
                d.recvHistory.add(zero);
                d.transHistory.add(zero);
                d.recvSpeed = d.transSpeed = d.recvLast = d.transLast = 0;
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Exception getting /proc/net/dev", e);
        }
    }

    public synchronized String getDevName(String deviceName) {
        Device d = devices.get(deviceName);
        if (null != d) {
            return d.deviceName;
        } else {
            return "";
        }
    }

    public synchronized long getTotalDown(String deviceName) {
        Device d = devices.get(deviceName);
        if (null != d) {
            return d.totalDown;
        } else {
            return 0L;
        }
    }

    public synchronized long getTotalUp(String deviceName) {
        Device d = devices.get(deviceName);
        if (null != d) {
            return d.totalUp;
        } else {
            return 0L;
        }
    }

    public synchronized long getDownSpeed(String deviceName) {
        Device d = devices.get(deviceName);
        if (null != d) {
            return d.recvSpeed;
        } else {
            return 0L;
        }
    }

    public synchronized long getUpSpeed(String deviceName) {
        Device d = devices.get(deviceName);
        if (null != d) {
            return d.transSpeed;
        } else {
            return 0L;
        }
    }

    public synchronized List<Graphable> getDownHistory(String deviceName) {
        Device d = devices.get(deviceName);
        if (null != d) {
            return new ArrayList<Graphable>(d.recvHistory);
        } else {
            return null;
        }
    }

    public synchronized List<Graphable> getUpHistory(String deviceName) {
        Device d = devices.get(deviceName);
        if (null != d) {
            return new ArrayList<Graphable>(d.transHistory);
        } else {
            return null;
        }
    }

    public void destroy(Context context) { }

    public boolean runWhenInvisible() {
        return true;
    }


    private Pattern netDevPattern;
    private Map<String, Device> devices;

    static final String PROC_NET_REGEX =
        "^\\s*(\\S+?):\\s*" + /* device */

        /* Receive */
        "(\\d+)\\s+" + /* bytes */
        "(\\d+)\\s+" + /* packets */
        "(\\d+)\\s+" + /* errs */
        "(\\d+)\\s+" + /* drop */
        "(\\d+)\\s+" + /* fifo */
        "(\\d+)\\s+" + /* frame */
        "(\\d+)\\s+" + /* compressed */
        "(\\d+)\\s+" + /* multicast */

        /* Transmit */
        "(\\d+)\\s+" + /* bytes */
        "(\\d+)\\s+" + /* packets */
        "(\\d+)\\s+" + /* errs */
        "(\\d+)\\s+" + /* drop */
        "(\\d+)\\s+" + /* fifo */
        "(\\d+)\\s+" + /* colls */
        "(\\d+)\\s+" + /* carrier */
        "(\\d+)\\s*";  /* compressed */

    static final String TAG = "ProcNetS";
    static final String TIWLAN0 = "tiwlan0";
    static final String WLAN0 = "wlan0";
    static final int MAX_HISTORY = 500;
    static final int INIT_BUFFER = 64;
}
