package org.moss.objects;

public class Time extends AbsMossObject {

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
        System.loadLibrary("native_functions");
    }
}
