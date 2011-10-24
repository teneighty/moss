package org.moss;

public class Common {

    public static void drawText(final Env env, final String txt) {
        if (null == txt || txt.length() == 0) {
            return;
        }

        float x = env.getX();
        float width = env.getPaint().measureText(txt, 0, txt.length());
        env.getCanvas().drawText(txt, x, env.getY() + env.getLineHeight(), env.getPaint());
        env.setX(x + width);
    }

    public static String formatSeconds(long seconds) {
        long days;
        int hours, minutes;

        days = seconds / 86400;
        seconds %= 86400;
        hours = (int) (seconds / 3600);
        seconds %= 3600;
        minutes = (int) (seconds / 60);
        seconds %= 60;

        if (days > 0) {
            return String.format("%dd, %dh %dm %ds", days, hours, minutes, seconds);
        } else {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        }
    }

    public static String humanReadble(long num) {
        int is = 0;
        while (num / 1024 >= 1 && is < suffixes.length) {
            num /= 1024;
            is++;
        }
        float fnum = (float) num;
        return String.format("%.0f%s", fnum, suffixes[is]);
    }

    public static int hexToInt(final String hex, int i) {
        if (hex == null) {
            return i;
        }
        String h = hex.replaceAll("(?i)\\d*x", "");
        h = h.replaceAll("#", "");
        try {
            return Long.valueOf(h, 16).intValue();
        } catch (NumberFormatException e) {
            return i;
        }
    }

    public static long toLong(String s) {
        return new Long(s).longValue();
    }

    public static float toFloat(String s) {
        return new Float(s).floatValue();
    }

    static String[] suffixes = {"B", "KiB", "MiB", "GiB", "TiB", "PiB", ""};
}
