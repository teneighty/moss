package org.moss.objects;

import org.moss.Env;
import org.moss.util.Bar;

abstract class AbsBarObject {

    /**
     * TODO: Add error checking
     */
    AbsBarObject() {
        this.height = 14f;
        this.width = 150f;
    }

    AbsBarObject(String hw) {
        String[] hwarr = hw.split(",");
        if (hwarr.length == 2) {
            this.height = new Float(hwarr[0]).floatValue();
            this.width = new Float(hwarr[1]).floatValue();
        } else {
            this.height = new Float(hwarr[0]).floatValue();
            this.width = -1.0f;
        }
    }

    protected void doDraw(Env env, float perc) {
        Bar b = new Bar();
        b.drawBar(env, perc, height, width);
    }

    protected float height;
    protected float width;
}

