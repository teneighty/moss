package org.mosspaper.objects;

import org.mosspaper.Env;
import org.mosspaper.ParseException;
import org.mosspaper.objects.ProcList.Proc;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class PortMon extends AbsMossObject implements MossObject {

    public PortMon(String startPort, String endPort, String var, String num) throws ParseException {
        this.var = var;
        this.num = Integer.parseInt(num);
        try {
            this.sp = Integer.parseInt(startPort);
            this.ep = Integer.parseInt(endPort);

            this.item = portMon.registerMonitor(var, sp, ep);
        } catch (NumberFormatException e) {
            new ParseException("Invalid port range");
        }
    }

    public DataProvider getDataProvider() {
        return this.portMon;
    }

    @Override
    public String toString() {
        if (item > 0) {
            return portMon.monpeek(sp, ep, item, num);
        } else {
            return "";
        }
    }

    protected String var;
    protected int num;
    protected int item;
    protected int sp;
    protected int ep;

    static PortMonProvider portMon = PortMonProvider.INSTANCE;
}
