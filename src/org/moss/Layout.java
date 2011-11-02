package org.moss;

import android.graphics.Canvas;

import org.moss.Parser.NewLine;
import org.moss.objects.MossObject;
import org.moss.objects.AlignR;
import org.moss.objects.AlignC;

import java.util.ArrayList;
import java.util.List;

public class Layout extends ArrayList<MossObject> implements List<MossObject> {

    public void draw(Canvas c, Env env) {
        List <MossObject> objs = this;
        for (MossObject o : objs) {
            o.preDraw(env);
        }
        int pos = 0;
        for (MossObject o : objs) {
            if (o instanceof AlignR
                    || o instanceof AlignC) {
                /* Create dummy env */
                Env tmpEnv = env.dummyClone();
                /* Draw from here to end of line */
                int start = pos;
                for (int i = start;; i++) {
                    MossObject m = objs.get(i);
                    if (m instanceof NewLine) {
                        break;
                    }
                    m.draw(tmpEnv);
                }
                float delta = tmpEnv.getX() - env.getX();
                /* Align right  */
                if (o instanceof AlignR) {
                    if (env.getMaxX() > delta 
                            && env.getX() < env.getMaxX() - delta) {
                        env.setX(env.getMaxX() - delta);
                    }
                } else {
                    float x = (env.getMaxX() / 2.0f) - (delta / 2.0f);
                    env.setX(x);
                }
            } else {
                o.draw(env);
            }
            pos++;
        }
        for (MossObject o : objs) {
            o.postDraw(env);
        }
    }
}

