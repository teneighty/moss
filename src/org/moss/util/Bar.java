package org.moss.util;

import android.graphics.Paint;
import android.graphics.Paint.Style;

import org.moss.Env;

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

        float boxWidth = width;
        float barWidth = boxWidth * perc;

        float x = env.getX();
        float y = env.getY() + height;
        env.setLineHeight(height);

        /* Draw Outline */
        env.getCanvas().drawRect(x, env.getY() + PADDING, x + boxWidth, y - PADDING, p);

        if (-1 != env.getConfig().getShadeColor()) {
            int c = env.getConfig().getShadeColor();
            c |= 0xFF000000;
            p.setColor(c);
        }

        p.setStyle(Style.FILL);

        /* Draw Bar */
        env.getCanvas().drawRect(x, env.getY() + PADDING, x + barWidth, y - PADDING, env.getPaint());

        p.setStyle(origStyle);
        p.setColor(origColor);
        env.setX(x + boxWidth);
    }

    static final float PADDING = 2;
}
