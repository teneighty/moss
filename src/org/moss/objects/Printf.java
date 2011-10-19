package org.moss.objects;

import org.moss.ParseException;

public class Printf extends AbsMossObject implements MossObject {

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

    @Override
    public String toString() {
        String[] strs = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            strs[i] = nvl(objects[i].toString(), "");
        }
        return String.format(format, (Object[]) strs);
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
