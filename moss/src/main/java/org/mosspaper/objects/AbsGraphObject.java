package org.mosspaper.objects;

import org.mosspaper.Env;
import org.mosspaper.util.Graph;

import java.util.List;

abstract class AbsGraphObject {

    AbsGraphObject(String color1, String color2) {
        this.height = 32f;
        this.width = 150f;
        this.colorLeft = Color.lookupColor(color1);
        this.colorRight = Color.lookupColor(color2);
    }

    AbsGraphObject(String hw, String color1, String color2) {
        String[] hwarr = hw.split(",");
        this.height = new Float(hwarr[0]).floatValue();
        this.width = new Float(hwarr[1]).floatValue();
        this.colorLeft = Color.lookupColor(color1);
        this.colorRight = Color.lookupColor(color2);
    }

    public void draw(Env env) {
        Graph g = new Graph();
        g.setHeight(height);
        g.setWidth(width);
        g.setScale(scale);
        g.setColorLeft(colorLeft);
        g.setColorRight(colorRight);
        g.setData(history);
        g.draw(env);
    }

    protected Integer colorLeft;
    protected Integer colorRight;
    protected float height;
    protected float width;
    protected int scale;
    protected List<Graphable> history;
}

