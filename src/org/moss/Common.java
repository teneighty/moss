package org.moss;

import android.graphics.Paint;
import android.graphics.Paint.Style;

import org.moss.objects.Color;

public class Common {

    public static void drawText(final Env env, final String txt) {
        if (null == txt || txt.length() == 0) {
            return;
        }

        float x = env.getX();
        float width = env.getPaint().measureText(txt, 0, txt.length());
        env.getCanvas().drawText(txt, x, env.getY(), env.getPaint());
        env.setX(x + width);
    }

    public static void drawBar(final Env env, final float perc) {
        drawBar(env, perc, env.getLineHeight(), env.getMaxX() - env.getX());
    }

    public static void drawBar(final Env env, final float perc, final float height, final float width) {
        final Paint p = env.getPaint();
        final int origColor = p.getColor();
        final Style origStyle = p.getStyle();

        String defaultBorderColor = env.getConfig().getOutlineColor();
        String defaultShadeColor = env.getConfig().getShadeColor();

        if (null != defaultBorderColor) {
            int c = Color.lookupColor(defaultBorderColor);
            if (c <= -1) {
                c = origColor;
            }
            c |= 0xFF000000;
            p.setColor(c);
        }
        p.setStyle(Style.STROKE);

        float boxWidth = width;
        float barWidth = boxWidth * perc;

        float x = env.getX();
        float y = env.getY() - p.getTextSize() + PADDING;
        // float y = (env.getY() - p.getTextSize() + PADDING) + height - PADDING;
        env.setLineHeight((height - p.getTextSize()) + p.getTextSize());

        /* Draw Outline */
        env.getCanvas().drawRect(x, y, env.getMaxX(), env.getY() - PADDING, p);

        if (null != defaultShadeColor) {
            int c = Color.lookupColor(defaultShadeColor);
            if (c <= -1) {
                c = origColor;
            }
            c |= 0xFF000000;
            p.setColor(c);
        }

        p.setStyle(Style.FILL);

        /* Draw Bar */
        env.getCanvas().drawRect(x, y, x + barWidth, env.getY() - PADDING, env.getPaint());

        p.setStyle(origStyle);
        p.setColor(origColor);
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
    static final float PADDING = 2;
}
