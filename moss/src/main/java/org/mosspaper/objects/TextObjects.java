package org.mosspaper.objects;

import android.util.Log;

import org.mosspaper.ParseException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextObjects {

    static final String TAG = "TextObjects";

    static class Args {

        protected Class mClass;

        Args(Class clazz) {
            this.mClass = clazz;
        }

        MossObject newInstance(String ident, List<Object> args) throws Exception {
            Class[] sigs = new Class[args.size()];
            Object[] nargs = new Object[args.size()];

            for (int i = 0; i < args.size(); ++i) {
                nargs[i] = (Object) args.get(i);
                if (args.get(i) instanceof MossObject) {
                    sigs[i] = MossObject.class;
                } else {
                    sigs[i] = args.get(i).getClass();
                }
            }
            Constructor<MossObject> con = mClass.getDeclaredConstructor(sigs);
            return con.newInstance(nargs);
        }

        static Args def(Class c) {
            return new Args(c);
        }
    }

    static class JoinArgs extends Args {

        JoinArgs(Class clazz) {
            super(clazz);
        }

        MossObject newInstance(String ident, List<Object> args) throws Exception {
            if (args.size() > 0) {
                Class[] sigs = new Class[] {String.class};
                Constructor<MossObject> con = mClass.getDeclaredConstructor(sigs);
                return con.newInstance(join(args));
            } else {
                Constructor<MossObject> con = mClass.getDeclaredConstructor();
                return con.newInstance();
            }
        }
    }

    static class ColorArgs extends Args {

        ColorArgs(String color) {
            super(Color.class);
            this.mColor = color;
        }

        MossObject newInstance(String ident, List<Object> _args) throws Exception {
            Class[] sigs = new Class[] {String.class};
            Constructor<MossObject> con = mClass.getDeclaredConstructor(sigs);
            return con.newInstance(mColor);
        }

        private String mColor;
    }

    static class FontArgs extends Args {
        FontArgs(String font) {
            super(Font.class);
            this.mFont = font;
        }

        MossObject newInstance(String ident, List<Object> _args) throws Exception {
            Class[] sigs = new Class[] {String.class};
            Constructor<MossObject> con = mClass.getDeclaredConstructor(sigs);
            return con.newInstance(mFont);
        }

        private String mFont;
    }

    static class PrintfArgs extends Args {

        PrintfArgs(Class clazz) {
            super(clazz);
        }

        MossObject newInstance(String ident, List<Object> args) throws Exception {
            Class[] sigs = new Class[] {String.class, Object[].class};
            Object[] nargs = new Object[2];
            nargs[0] = (String) args.get(0);
            Object[] nsubargs = new Object[args.size() - 1];
            for (int i = 1; i < args.size(); ++i) {
                nsubargs[i - 1] = args.get(i);
            }
            nargs[1] = nsubargs;
            Constructor<MossObject> con = mClass.getDeclaredConstructor(sigs);
            return con.newInstance(nargs);
        }
    }

    public static MossObject inst(String ident, List<Object> args) throws ParseException {
        Args pa = TEXT_OBJECTS.get(ident);
        if (null == pa) {
            ParseException pe = new ParseException("No such object");
            pe.setIdent(ident);
            throw pe;
        }
        try {
            return pa.newInstance(ident, args);
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
                Log.i(TAG, "Instantiation exception... "  + ident + ", args length: " + args.size());
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

    static Map<String, Args> TEXT_OBJECTS;
    static {
        TEXT_OBJECTS = new HashMap<String, Args>();

        TEXT_OBJECTS.put("interval", Args.def(Interval.class));
        TEXT_OBJECTS.put("printf", new PrintfArgs(Printf.class));
        TEXT_OBJECTS.put("length", Args.def(Length.class));
        TEXT_OBJECTS.put("alignr", Args.def(AlignR.class));
        TEXT_OBJECTS.put("alignc", Args.def(AlignC.class));

        TEXT_OBJECTS.put("color", Args.def(Color.class));
        TEXT_OBJECTS.put("hr", Args.def(HRule.class));
        TEXT_OBJECTS.put("stippled_hr", Args.def(StippledHRule.class));

        TEXT_OBJECTS.put("sysname", Args.def(SysName.class));
        TEXT_OBJECTS.put("nodename", Args.def(Nodename.class));
        TEXT_OBJECTS.put("kernel", Args.def(Kernel.class));
        TEXT_OBJECTS.put("machine", Args.def(Machine.class));
        TEXT_OBJECTS.put("uptime", Args.def(Uptime.class));
        TEXT_OBJECTS.put("realtime", Args.def(Realtime.class));
        TEXT_OBJECTS.put("loadavg", Args.def(LoadAvg.class));

        TEXT_OBJECTS.put("mem", Args.def(Mem.class));
        TEXT_OBJECTS.put("memmax", Args.def(MemMax.class));
        TEXT_OBJECTS.put("memperc", Args.def(MemPerc.class));
        TEXT_OBJECTS.put("membar", Args.def(MemBar.class));
        TEXT_OBJECTS.put("swap", Args.def(Swap.class));
        TEXT_OBJECTS.put("swapmax", Args.def(SwapMax.class));
        TEXT_OBJECTS.put("swapperc", Args.def(SwapPerc.class));
        TEXT_OBJECTS.put("swapbar", Args.def(SwapBar.class));

        TEXT_OBJECTS.put("processes", Args.def(Processes.class));
        TEXT_OBJECTS.put("running_processes", Args.def(ProcsRunning.class));

        TEXT_OBJECTS.put("top", Args.def(Top.class));
        TEXT_OBJECTS.put("top_mem", Args.def(TopMem.class));

        TEXT_OBJECTS.put("fs_free", Args.def(FSFree.class));
        TEXT_OBJECTS.put("fs_used", Args.def(FSUsed.class));
        TEXT_OBJECTS.put("fs_used_perc", Args.def(FSUsedPerc.class));
        TEXT_OBJECTS.put("fs_size", Args.def(FSSize.class));
        TEXT_OBJECTS.put("fs_bar", Args.def(FSBar.class));

        TEXT_OBJECTS.put("freq", Args.def(Freq.class));
        TEXT_OBJECTS.put("cpu", Args.def(Cpu.class));
        TEXT_OBJECTS.put("cpubar", Args.def(CpuBar.class));
        TEXT_OBJECTS.put("cpugraph", Args.def(CpuGraph.class));

        TEXT_OBJECTS.put("battery", Args.def(Battery.class));
        TEXT_OBJECTS.put("battery_bar", Args.def(BatteryBar.class));
        TEXT_OBJECTS.put("battery_percent", Args.def(BatteryPercent.class));
        /* TODO: fix battery time */
        // TEXT_OBJECTS.put("battery_time", BatteryTime.class);

        TEXT_OBJECTS.put("devname", Args.def(DevName.class));
        TEXT_OBJECTS.put("downspeed", Args.def(DownSpeed.class));
        TEXT_OBJECTS.put("downspeedgraph", Args.def(DownSpeedGraph.class));
        TEXT_OBJECTS.put("totaldown", Args.def(TotalDown.class));
        TEXT_OBJECTS.put("upspeed", Args.def(UpSpeed.class));
        TEXT_OBJECTS.put("upspeedgraph", Args.def(UpSpeedGraph.class));
        TEXT_OBJECTS.put("totalup", Args.def(TotalUp.class));
        TEXT_OBJECTS.put("tcp_portmon", Args.def(PortMon.class));

        TEXT_OBJECTS.put("entropy_avail", Args.def(Entropy.Available.class));
        TEXT_OBJECTS.put("entropy_bar", Args.def(Entropy.Bar.class));
        TEXT_OBJECTS.put("entropy_perc", Args.def(Entropy.Percent.class));
        TEXT_OBJECTS.put("entropy_poolsize", Args.def(Entropy.PoolSize.class));

        TEXT_OBJECTS.put("image", Args.def(Image.class));
        TEXT_OBJECTS.put("goto", Args.def(Goto.class));
        TEXT_OBJECTS.put("vgoto", Args.def(VGoto.class));
        TEXT_OBJECTS.put("offset", Args.def(Offset.class));
        TEXT_OBJECTS.put("voffset", Args.def(VOffset.class));
        TEXT_OBJECTS.put("font", new JoinArgs(Font.class));
        TEXT_OBJECTS.put("time", new JoinArgs(Time.class));
    }
}
