package org.mosspaper.objects;

import org.mosspaper.ParseException;

public class LoadAvg extends AbsMossObject implements MossObject {

    /**
     * Display the 1, 5 and 15 minute load avgerage of jobs in the run queue.
     */
    public LoadAvg() {
        this.idx = -1;
    }

    /**
     * Display either 1, 5 or 15 minute load avgerage of jobs in the run queue.
     *
     * @param idx An integer value, 1 &gte; idx &lte; 3.
     */
    public LoadAvg(String idx) throws ParseException {
        try {
            this.idx = Integer.parseInt(idx);
            if (this.idx < 1 || this.idx > 3) {
                throw new ParseException(String.format("Invalid index: '%s'", idx));
            }
        }  catch (NumberFormatException e) {
            throw new ParseException(String.format("Invalid index: '%s'", idx));
        }
    }

    public DataProvider getDataProvider() {
        return ProcLoadAvg.INSTANCE;
    }

    @Override
    public String toString() {
        String[] loadAvg = ProcLoadAvg.INSTANCE.getLoadAvg();
        if (idx == -1 && loadAvg.length == 3) {
            return String.format("%s %s %s", loadAvg[0], loadAvg[1], loadAvg[2]);
        } else if (idx < loadAvg.length) {
            return String.format("%s", loadAvg[idx]);
        } else {
            return "Unavailable";
        }
    }

    private int idx;
}
