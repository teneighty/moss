package org.moss.objects;

import org.moss.Env;

public interface MossObject {

    public void preDraw(Env env);

    public void draw(Env env);

    public void postDraw(Env env);

    public DataProvider getDataProvider();

}
