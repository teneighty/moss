package org.mosspaper.objects;

import android.util.Log;

import org.mosspaper.Env;
import org.mosspaper.ParseException;

import java.util.IllegalFormatException;

public class Length extends AbsMossObject implements MossObject {

    /**
     * Limit an objects output to a certain number of characters.
     *
     * @param minLength minimum length of the string
     * @param maxLength max length of the string
     * @param object a text object whose output will be limited to N characters
     */
    public Length(String minLength, String maxLength, MossObject object) throws ParseException {
        try {
            this.min = Integer.parseInt(minLength);
        } catch (NumberFormatException e) {
            throw new ParseException(String.format("Incorrect length attribute %s", minLength));
        }
        try {
            this.max = Integer.parseInt(maxLength);
        } catch (NumberFormatException e) {
            throw new ParseException(String.format("Incorrect length attribute %s", maxLength));
        }
        this.object = object;
        if (!(this.object instanceof AbsMossObject)) {
            throw new ParseException("Incorrect type");
        }
    }

    public DataProvider getDataProvider() {
        return null;
    }

    public void preDraw(Env env) {
        object.preDraw(env);
    }

    public void postDraw(Env env) {
        object.postDraw(env);
    }

    @Override
    public String toString() {
        String v = object.toString();
        if (null == v) {
            v = "";
        } 
        return String.format("%-" + min + "." + max + "s", v);
    }

    private MossObject object;
    private int min;
    private int max;
}
