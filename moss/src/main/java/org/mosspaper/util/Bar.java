package org.mosspaper.util;

import android.graphics.Paint;
import android.graphics.Paint.Style;

import org.mosspaper.Env;

public class Bar {

    public Bar() { }

    public void drawBar(final Env env, final float perc) {
        drawBar(env, perc, env.getLineHeight(), env.getMaxX() - env.getX());
    }

    public void drawBar(final Env env, final float perc, final float height, final float width) {
        final Paint p = env.getPaint();
        final int origColor = p.getColor();
        final Style origStyle = p.getStyle();

        if (-1 != env.getConfig().getOutlineColor()) {
            int c = env.getConfig().getOutlineColor();
            c |= 0xFF000000;
            p.setColor(c);
        }
        p.setStyle(Style.STROKE);

        float boxWidth = (width == -1.0f) ? env.getMaxX() : width;
        float barWidth = boxWidth * perc;

        float x = env.getX();
        float top = env.getY() + PADDING;
        float y = top + height;
        env.setLineHeight(height);

        /* Draw Outline */
        env.getCanvas().drawRect(x, top, x + boxWidth, y, p);

        if (-1 != env.getConfig().getShadeColor()) {
            int c = env.getConfig().getShadeColor();
            c |= 0xFF000000;
            p.setColor(c);
        }

        p.setStyle(Style.FILL);

        /* Draw Bar */
        env.getCanvas().drawRect(x, top, x + barWidth, y, env.getPaint());

        p.setStyle(origStyle);
        p.setColor(origColor);
        env.setX(x + boxWidth);
    }

    static float PADDING = 2.0f;
}
