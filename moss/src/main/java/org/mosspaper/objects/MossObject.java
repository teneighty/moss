package org.mosspaper.objects;

import org.mosspaper.Env;

public interface MossObject {

    public void preDraw(Env env);

    public void draw(Env env);

    public void postDraw(Env env);

    public DataProvider getDataProvider();

}
