package org.mosspaper.objects;

import org.mosspaper.Env;
import org.mosspaper.Common;

public abstract class AbsMossObject implements MossObject {

    public void preDraw(Env env) { }

    public void draw(Env env) {
        Common.drawText(env, toString());
    }

    public void postDraw(Env env) { }

    public DataProvider getDataProvider() {
        return null;
    }

    public abstract String toString();
}
