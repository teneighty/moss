package org.mosspaper.objects;

import org.mosspaper.ParseException;

public class Interval extends AbsMossObject implements MossObject {

    /**
     * Changed the current update interval.
     */
    public Interval(String i) throws ParseException {
        try {
            float tmp = new Float(i).floatValue();
            this.interval = (long) (tmp * 1000.0f);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid interval");
        }
    }

    public DataProvider getDataProvider() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

    public long getInterval() {
        return interval;
    }

    private long interval;
}
