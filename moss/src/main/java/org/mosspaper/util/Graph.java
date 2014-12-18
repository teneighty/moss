package org.mosspaper.util;

import android.graphics.Paint;
import android.graphics.Paint.Style;

import org.mosspaper.Env;
import org.mosspaper.objects.Graphable;

import java.util.List;

public class Graph {

    public Graph() {
        this.colorLeft = 0x000000;
        this.colorRight = 0x5000a0;
    }

    public Graph setData(List<Graphable> data) {
        this.data = data;
        return this;
    }

    public Graph setHeight(float height) {
        this.height = height;
        return this;
    }

    public Graph setWidth(float width) {
        this.width = width;
        return this;
    }

    public Graph setScale(int scale) {
        this.scale = scale;
        return this;
    }

    public Graph setColorLeft(int colorLeft) {
        this.colorLeft = colorLeft;
        return this;
    }

    public Graph setColorRight(int colorRight) {
        this.colorRight = colorRight;
        return this;
    }

    public void draw(Env env) {
        final Paint p = env.getPaint();
        final int origColor = p.getColor();
        final Style origStyle = p.getStyle();

        if (-1 != env.getConfig().getOutlineColor()) {
            int c = env.getConfig().getOutlineColor();
            c |= 0xFF000000;
            p.setColor(c);
        }
        p.setStyle(Style.STROKE);

        float x = env.getX();
        float y = env.getY();

        if (width <= 0) {
            width = env.getMaxX() - x;
        }
        float maxX = x + (width > 0 ? width : env.getMaxX());
        float maxY = y + height;
        env.setLineHeight(height);

        /* Draw Outline */
        env.getCanvas().drawRect(x, y, maxX, maxY, p);

        p.setStyle(Style.FILL);

        /* Move inside border */
        maxX -= 1.0f;
        maxY -= 1.0f;
        width -= 2.0f;

        /* Start from right */
        if (null != data) {
            long max = 0L;
            int count = 0;
            if (scale > 0) {
                max = scale;
            } else {
                for (Graphable g : data) {
                    if (count > width) {
                        break;
                    }
                    max = Math.max(max, g.getValue());
                    count++;
                }
            }
            count = 0;
            int nextColor = colorRight;
            for (Graphable g : data) {
                float currx = maxX - count;
                float curry = maxY;
                if (max > 0f) {
                    float frac = Math.min(1.0f, g.getValue() / (float) max);
                    float diff = maxY - y;
                    curry = maxY - (frac * diff);
                }
                if (curry < 0 || curry > maxY) {
                    curry = maxY;
                }
                p.setColor(nextColor);
                p.setAlpha(0xff);
                env.getCanvas().drawLine(currx, curry, currx, maxY, p);
                nextColor = calcNextColor((int) width - count, colorLeft, nextColor);
                if (count > width) {
                    break;
                }
                count++;
            }
        }

        p.setStyle(origStyle);
        p.setColor(origColor);
        env.setX(maxX);
    }

    int calcNextColor(int width, int cl, int cr) {
        if (cl == cr || width <= 0) {
            return cl;
        }

        /* mask off and shift each channel */
        int redl = (cl & 0xff0000) >> 0x10;
        int greenl = (cl & 0xff00) >> 0x08;
        int bluel = cl & 0xff;

        int redr = (cr & 0xff0000) >> 0x10;
        int greenr = (cr & 0xff00) >> 0x08;
        int bluer = cr & 0xff;

        int red = redr - ((redr - redl) / width);
        int green = greenr - ((greenr - greenl) / width);
        int blue = bluer - ((bluer - bluel) / width);

        int v = (red << 16) + (green << 8) + blue;
        return v;
    }

    private List<Graphable> data;
    private float height;
    private float width;
    private int scale;
    private int colorLeft;
    private int colorRight;
}
