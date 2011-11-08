package org.mosspaper;

import android.graphics.Canvas;

import org.mosspaper.objects.MossObject;

import java.util.ArrayList;
import java.util.List;

public class Layout extends ArrayList<MossObject> implements List<MossObject> {

    public void draw(Canvas c, Env env) {
        List <MossObject> objs = this;
        for (MossObject o : objs) {
            o.preDraw(env);
        }
        for (MossObject o : objs) {
            o.draw(env);
        }
        for (MossObject o : objs) {
            o.postDraw(env);
        }
    }
}

