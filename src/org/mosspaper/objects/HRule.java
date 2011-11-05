package org.mosspaper.objects;

import android.graphics.Paint;
import android.graphics.Paint.Style;

import org.mosspaper.Env;
import org.mosspaper.ParseException;

public class HRule implements MossObject {

    /**
     * Draw a horizontal rule using the default line height.
     */
    public HRule() {
        lineHeight = DEF_HEIGHT;
    }

    /**
     * Draw a horizontal rule using a line height of lh.
     *
     * @param lh a number, used as the new line height
     */
    public HRule(String lh) throws ParseException {
        try {
            lineHeight = new Float(lh).floatValue();
        } catch (NumberFormatException e) {
            throw new ParseException(String.format("Invalid line height: '%s'", lh));
        }
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        final Paint p = env.getPaint();
        Style s = p.getStyle();
        p.setStyle(Style.FILL);

        /* move cursor to bottom of text and draw up */
        float y = env.getY() + env.getLineHeight();
        env.getCanvas().drawRect(
            env.getX(), y,
            env.getMaxX(), y - lineHeight,
            env.getPaint());

        p.setStyle(s);
        // env.setX(env.getMaxX());
    }

    public void postDraw(Env env) { }

    public DataProvider getDataProvider() {
        return null;
    }

    private float lineHeight;
    static final float DEF_HEIGHT = 1f;
}
