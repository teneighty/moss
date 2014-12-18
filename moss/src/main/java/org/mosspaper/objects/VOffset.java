package org.mosspaper.objects;

import org.mosspaper.Env;
import org.mosspaper.ParseException;

public class VOffset implements MossObject {

    /**
     * Move text vertically by N pixels
     *
     * @param pixels the number of pixels
     */
    public VOffset(String pixels) throws ParseException {
        try {
            this.mPixels = Integer.parseInt(pixels);
        } catch (NumberFormatException e) {
            throw new ParseException(String.format("Invalid pixels: '%s'", pixels));
        }
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        env.setY(env.getY() + mPixels);
    }

    public void postDraw(Env env) { }

    public DataProvider getDataProvider() {
        return null;
    }

    private int mPixels;
}
