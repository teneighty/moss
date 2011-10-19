package org.moss;

public class ConfigException extends MossException {

    public ConfigException(String msg) {
        super(msg);
        type = ERROR;
    }
}
