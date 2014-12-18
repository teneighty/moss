package org.mosspaper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Common {

    public static void drawText(final Env env, String txt) {
        if (null == txt || txt.length() == 0) {
            return;
        }
        if (env.getConfig().getUppercase()) {
            txt = txt.toUpperCase();
        }

        float x = env.getX();
        float width = env.getPaint().measureText(txt, 0, txt.length());
        env.setLineHeight(env.getPaint().getTextSize());
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

    public static String formatSecondsShort(long seconds) {
        long days;
        int hours, minutes;

        days = seconds / 86400;
        seconds %= 86400;
        hours = (int) (seconds / 3600);
        seconds %= 3600;
        minutes = (int) (seconds / 60);
        seconds %= 60;

        if (days > 0) {
            return String.format("%dd, %dh", days, hours);
        } else if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm %ds", minutes, seconds);
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
            return Integer.parseInt(h, 16);
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

    public static String slurp(File file) throws IOException {
        String body = "";
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            body = slurp(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return body;
    }

    public static String slurp(InputStream is) throws IOException {
        String line;
        BufferedReader reader = null;
        StringBuffer buf = new StringBuffer("");
        try {
            reader = new BufferedReader(new InputStreamReader(is));

            while ((line = reader.readLine()) != null) {
                if (line.indexOf("#") == 0) {
                    buf.append("\n");
                    continue;
                }
                buf.append(line).append("\n");
            }
        } finally {
            if (null != is) {
                is.close();
            }
            if (null != reader) {
                reader.close();
            }
        }
        return buf.toString();
    }


    static String[] suffixes = {"B", "KiB", "MiB", "GiB", "TiB", "PiB", ""};
}
