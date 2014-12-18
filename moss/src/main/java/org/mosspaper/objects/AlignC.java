package org.mosspaper.objects;

import org.mosspaper.Env;

public class AlignC extends AbsAlign implements MossObject {

    /**
     * Align the text to center.
     */
    public AlignC() { }

    public void draw(Env env) { 

        if (curX != null && curX == lastX) {
            env.setX(curX);
            return;
        }

        Env tmpEnv = buildEnv(env);
        if (tmpEnv == null) {
            return;
        }

        float delta = tmpEnv.getX() - env.getX();

        lastX = curX;
        curX = (env.getMaxX() / 2.0f) - (delta / 2.0f);
        env.setX(curX);
    }

    Float curX;
    Float lastX;
}
