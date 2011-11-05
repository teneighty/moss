package org.mosspaper.objects;

public class Nodename extends AbsMossObject implements MossObject {

    /**
     * Display the system name
     */
    public Nodename() { }

    public DataProvider getDataProvider() {
        return uname;
    }

    @Override
    public String toString() {
        return uname.getNodename();
    }

    private UnameProvider uname = UnameProvider.INSTANCE;
}
