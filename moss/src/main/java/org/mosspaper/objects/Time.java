package org.mosspaper.objects;

public class Time extends AbsMossObject {

    /**
     * Prints the local time formatted according to parameter. 
     *
     * @param format uses C's strftime format to specify time format.
     */
    public Time(String format) {
        this.mFormat = format;
    }

    @Override
    public String toString() {
        return strftime(mFormat);
    }

    private native String strftime(String format);

    private String mFormat;

    static {
        System.loadLibrary("moss");
    }
}
