package org.mosspaper;

public class ParseException extends MossException {

    public ParseException(String msg) {
        super(msg);
        type = ERROR;
    }
}
