package org.moss.objects;

import org.moss.Env;
import org.moss.ParseException;

public class Offset implements MossObject {

    /**
     * Move text horizontally by N pixels
     *
     * @param pixels the number of pixels
     */
    public Offset(String pixels) throws ParseException {
        try {
            this.mPixels = new Integer(pixels).intValue();
        } catch (NumberFormatException e) {
            throw new ParseException(String.format("Invalid pixels: '%s'", pixels));
        }
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        env.setX(env.getX() + mPixels);
    }

    public void postDraw(Env env) { }

    public DataProvider getDataProvider() {
        return null;
    }

    private int mPixels;
}
