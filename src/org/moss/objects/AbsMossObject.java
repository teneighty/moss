package org.moss.objects;

import org.moss.Env;
import org.moss.Common;

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
