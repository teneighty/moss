package org.moss;

public class ParseException extends MossException {

    public ParseException(String msg) {
        super(msg);
        type = ERROR;
    }
}
