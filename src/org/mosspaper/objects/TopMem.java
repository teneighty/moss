package org.mosspaper.objects;

import org.mosspaper.objects.ProcList.Proc;

import java.util.List;
import java.util.Comparator;

public class TopMem extends AbsTop implements MossObject {

    /**
     * Display information about the top memory consuming processes. For
     * example to display the top three processes, and their mem usage:
     * <pre><code>
     *  ${top name 1}${top mem 1}
     *  ${top name 2}${top mem 2}
     *  ${top name 3}${top mem 3}
     * </code></pre>
     *
     * @param var The name of the variable to display
     * @param num An integer, &gte; 1
     */
    public TopMem(String var, String num) {
        super(var, num);
    }

    public Comparator getComparator() {
        return new Comparator<Proc>() {
            public int compare(Proc p1, Proc p2) {
                if (p1 == p2 || p1.memPerc == p2.memPerc) {
                    return 0;
                } else if (p1.memPerc < p2.memPerc) {
                    return -1;
                } else {
                    return 1;
                }
            }
        };
    }

    public void setList(List<Proc> ls) {
        this.ps = ls;
    }

    public List<Proc> getList() {
        return this.ps;
    }

    protected static List<Proc> ps;
}
