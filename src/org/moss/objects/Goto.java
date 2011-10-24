package org.moss.objects;

import org.moss.Env;
import org.moss.ParseException;

public class Goto implements MossObject {

    /**
     * Change current horizontal location to postition
     *
     * @param position the new position
     */
    public Goto(String pixels) throws ParseException {
        try {
            this.mPosition = new Integer(pixels).intValue();
        } catch (NumberFormatException e) {
            throw new ParseException(String.format("Invalid pixels: '%s'", pixels));
        }
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        env.setX(mPosition);
    }

    public void postDraw(Env env) { }

    public DataProvider getDataProvider() {
        return null;
    }

    private int mPosition;
}
