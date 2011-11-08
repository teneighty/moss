package org.mosspaper.objects;

import org.mosspaper.Env;

public class AlignR extends AbsAlign implements MossObject {

    /**
     * Align the text to the right.
     */
    public AlignR() { }

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

        if (env.getMaxX() > delta 
                && env.getX() < env.getMaxX() - delta) {
            lastX = curX;
            curX = env.getMaxX() - delta;
            env.setX(curX);
        }
    }

    Float curX;
    Float lastX;
}
