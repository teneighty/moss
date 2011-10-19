package org.moss.objects;

import android.util.Log;

import org.moss.ParseException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextObjects {

    static final String TAG = "TextObjects";

    public static MossObject inst(String ident, List<Object> args) throws ParseException {
        Class c = TEXT_OBJECTS.get(ident);
        if (null == c) {
            ParseException pe = new ParseException("No such object");
            pe.setIdent(ident);
            throw pe;
        }
        try {
            Class[] sigs = null;
            Object[] nargs = null;
            if ("printf".equals(ident)) {
                sigs = new Class[] {String.class, Object[].class};
                nargs = new Object[2];
                nargs[0] = (String) args.get(0);
                Object[] nsubargs = new Object[args.size() - 1];
                for (int i = 1; i < args.size(); ++i) {
                    nsubargs[i - 1] = args.get(i);
                }
                nargs[1] = nsubargs;
                Constructor<MossObject> con = c.getDeclaredConstructor(sigs);
                return con.newInstance(nargs);
            } else {
                sigs = new Class[args.size()];
                nargs = new Object[args.size()];

                for (int i = 0; i < args.size(); ++i) {
                    sigs[i] = args.get(i).getClass();
                    nargs[i] = (Object) args.get(i);
                }
                Constructor<MossObject> con = c.getDeclaredConstructor(sigs);
                return con.newInstance(nargs);
            }
        } catch (NoSuchMethodException e) {
            ParseException pe = new ParseException("Incorrect parameters");
            pe.setIdent(ident);
            throw pe;
        } catch (SecurityException e) {
            Log.i(TAG, "SecurityException " + ident + " " + args.size());
        } catch (Exception e) {
            if (e instanceof ParseException) {
                ParseException pe = (ParseException) e;
                pe.setIdent(ident);
                throw pe;
            } else {
                Log.i(TAG, "Instantiation exception... "  + ident + " " + args.size());
                Log.e(TAG, "", e);
                ParseException pe = new ParseException("Argument error: " + join(args));
                pe.setIdent(ident);
                throw pe;
            }
        }
        return null;
    }

    private static String join(List<Object> args) {
        StringBuffer buf = new StringBuffer("");
        for (Object o : args) {
            if (o instanceof String) {
                buf.append(o).append(" ");
            }
        }
        return buf.toString().replaceAll("\\s+$", "");
    }

    static Map<String, Class> TEXT_OBJECTS;
    static {
        TEXT_OBJECTS = new HashMap<String, Class>();

        TEXT_OBJECTS.put("interval", Interval.class);
        TEXT_OBJECTS.put("printf", Printf.class);

        TEXT_OBJECTS.put("color", Color.class);
        TEXT_OBJECTS.put("hr", HRule.class);
        TEXT_OBJECTS.put("stippled_hr", StippledHRule.class);

        TEXT_OBJECTS.put("sysname", SysName.class);
        TEXT_OBJECTS.put("kernel", Kernel.class);
        TEXT_OBJECTS.put("machine", Machine.class);
        TEXT_OBJECTS.put("uptime", Uptime.class);
        TEXT_OBJECTS.put("realtime", Realtime.class);
        TEXT_OBJECTS.put("loadavg", LoadAvg.class);

        TEXT_OBJECTS.put("mem", Mem.class);
        TEXT_OBJECTS.put("memmax", MemMax.class);
        TEXT_OBJECTS.put("memperc", MemPerc.class);
        TEXT_OBJECTS.put("membar", MemBar.class);
        TEXT_OBJECTS.put("swap", Swap.class);
        TEXT_OBJECTS.put("swapmax", SwapMax.class);
        TEXT_OBJECTS.put("swapperc", SwapPerc.class);
        TEXT_OBJECTS.put("swapbar", SwapBar.class);

        TEXT_OBJECTS.put("processes", Processes.class);
        TEXT_OBJECTS.put("running_processes", ProcsRunning.class);

        TEXT_OBJECTS.put("top", Top.class);
        TEXT_OBJECTS.put("top_mem", TopMem.class);

        TEXT_OBJECTS.put("fs_free", FSFree.class);
        TEXT_OBJECTS.put("fs_used", FSUsed.class);
        TEXT_OBJECTS.put("fs_size", FSSize.class);
        TEXT_OBJECTS.put("fs_bar", FSBar.class);

        TEXT_OBJECTS.put("cpu", Cpu.class);
        TEXT_OBJECTS.put("cpubar", CpuBar.class);
        TEXT_OBJECTS.put("cpugraph", CpuGraph.class);

        TEXT_OBJECTS.put("battery", Battery.class);
        TEXT_OBJECTS.put("battery_bar", BatteryBar.class);
        TEXT_OBJECTS.put("battery_percent", BatteryPercent.class);
        /* TODO: fix battery time */
        // TEXT_OBJECTS.put("battery_time", BatteryTime.class);

        TEXT_OBJECTS.put("downspeed", DownSpeed.class);
        TEXT_OBJECTS.put("downspeedgraph", DownSpeedGraph.class);
        TEXT_OBJECTS.put("upspeed", UpSpeed.class);
        TEXT_OBJECTS.put("upspeedgraph", UpSpeedGraph.class);
    }
}
