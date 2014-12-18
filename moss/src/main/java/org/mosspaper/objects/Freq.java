package org.mosspaper.objects;

import org.mosspaper.ParseException;

public class Freq extends AbsMossObject implements MossObject {

    /**
     * Returns firts CPU's frequency in MHz.
     */
    public Freq() throws ParseException { 
        this.cpuNum = 1;
        freqInfo.registerCpu(cpuNum);
    }

    /**
     * Returns CPU n's frequency in MHz. 
     *
     * @param cpuNum the cpu number
     */
    public Freq(String cpuNum) throws ParseException { 
        try {
            this.cpuNum = Integer.parseInt(cpuNum);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid cpu number");
        }
        freqInfo.registerCpu(this.cpuNum);
    }

    public DataProvider getDataProvider() {
        return freqInfo;
    }

    @Override
    public String toString() {
        return String.format("%.2f", freqInfo.getFreq(cpuNum) / 1000.0);
    }

    private CpuFreqProvider freqInfo = CpuFreqProvider.INSTANCE;
    private int cpuNum;
}
