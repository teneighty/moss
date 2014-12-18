package org.mosspaper.objects;

public class Machine extends AbsMossObject implements MossObject {

    /**
     * Display the machine name
     */
    public Machine() { }

    public DataProvider getDataProvider() {
        return uname;
    }

    @Override
    public String toString() {
        return uname.getMachine();
    }

    private UnameProvider uname = UnameProvider.INSTANCE;
}
