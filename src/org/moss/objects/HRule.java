package org.moss.objects;

import org.moss.Env;
import org.moss.ParseException;

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
        env.getCanvas().drawRect(
            env.getX(), env.getY() + PADDING,
            env.getMaxX(), env.getY() + lineHeight + PADDING,
            env.getPaint());
            env.setY(env.getY() + env.getPaint().getTextSize() + PADDING);
    }

    public void postDraw(Env env) { }

    public DataProvider getDataProvider() {
        return null;
    }

    private float lineHeight;
    static final float DEF_HEIGHT = 1f;
    static final float PADDING = 10.0f;
}
