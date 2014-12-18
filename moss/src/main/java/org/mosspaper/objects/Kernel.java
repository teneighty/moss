package org.mosspaper.objects;

public class Kernel extends AbsMossObject implements MossObject {

    /**
     * Display the kernel version.
     */
    public Kernel() { }

    public DataProvider getDataProvider() {
        return uname;
    }

    @Override
    public String toString() {
        return uname.getRelease();
    }

    private UnameProvider uname = UnameProvider.INSTANCE;
}
