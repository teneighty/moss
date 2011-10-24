package org.moss.objects;

import android.util.Log;

import org.moss.Env;
import org.moss.ParseException;

import java.util.IllegalFormatException;

public class Printf extends AbsMossObject implements MossObject {

    static final String TAG = "Printf";

    /**
     * Accepts a format string and other text objects.
     *
     * @param format a format string as describe in...
     * @param objects a variable list of text objects
     */
    public Printf(String format, Object... objects) throws ParseException {
        this.format = format;
        this.objects = objects;
        for (Object o : objects) {
            if (o instanceof MossObject
                    && !(o instanceof AbsMossObject)) {
                throw new ParseException("Incorrect type");
            }
        }
    }

    public DataProvider getDataProvider() {
        return null;
    }

    public void preDraw(Env env) {
        for (Object o : objects) {
            if (o instanceof MossObject) {
                ((MossObject) o).preDraw(env);
            }
        }
    }

    public void postDraw(Env env) {
        for (Object o : objects) {
            if (o instanceof MossObject) {
                ((MossObject) o).postDraw(env);
            }
        }
    }

    @Override
    public String toString() {
        String[] strs = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            strs[i] = nvl(objects[i].toString(), "");
        }
        try {
            return String.format(format, (Object[]) strs);
        } catch (IllegalFormatException e) {
            Log.e(TAG, "", e);
        } catch (NullPointerException e) {
            Log.e(TAG, "", e);
        }
        return "";
    }

    private String nvl(String s1, String s2) {
        if (null == s1) {
            return s2;
        } else {
            return s1;
        }
    }

    private String format;
    private Object[] objects;
}
