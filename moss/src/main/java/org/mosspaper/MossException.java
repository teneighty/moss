package org.mosspaper;

public class MossException extends Exception {

    public MossException(String msg) {
        super(msg);
        type = ERROR;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getIdent() {
        return ident;
    }

    public void setLineNo(int n) {
        this.lineNo = n;
    }

    public void setColNo(int n) {
        this.colNo = n;
    }

    public String getBrief() {
        return String.format("Line %d:%d: %s", lineNo, colNo, ident);
    }

    public String getSummary() {
        return getMessage();
    }

    public void setErrType(int type) {
        this.type = type;
    }

    public int getErrType() {
        return type;
    }

    String ident;
    int lineNo;
    int colNo;
    int type;

    public static final int ERROR = 1;
    public static final int WARNING = 2;
}
