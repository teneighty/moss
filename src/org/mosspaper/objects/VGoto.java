package org.mosspaper.objects;

import org.mosspaper.Env;
import org.mosspaper.ParseException;

public class VGoto implements MossObject {

    /**
     * Change current vertical location to postition
     *
     * @param position the new position
     */
    public VGoto(String position) throws ParseException {
        try {
            this.mPosition = Integer.parseInt(position);
        } catch (NumberFormatException e) {
            throw new ParseException(String.format("Invalid position: '%s'", position));
        }
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        env.setY(mPosition);
    }

    public void postDraw(Env env) { }

    public DataProvider getDataProvider() {
        return null;
    }

    private int mPosition;
}
