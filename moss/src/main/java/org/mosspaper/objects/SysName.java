package org.mosspaper.objects;

public class SysName extends AbsMossObject implements MossObject {

    /**
     * Display the system name
     */
    public SysName() { }

    public DataProvider getDataProvider() {
        return uname;
    }

    @Override
    public String toString() {
        return uname.getSysname();
    }

    private UnameProvider uname = UnameProvider.INSTANCE;
}
