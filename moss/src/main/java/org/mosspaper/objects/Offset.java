package org.mosspaper.objects;

import org.mosspaper.Env;
import org.mosspaper.ParseException;

public class Offset implements MossObject {

    /**
     * Move text horizontally by N pixels
     *
     * @param pixels the number of pixels
     */
    public Offset(String pixels) throws ParseException {
        try {
            this.mPixels = Integer.parseInt(pixels);
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
