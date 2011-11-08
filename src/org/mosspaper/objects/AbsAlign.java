package org.mosspaper.objects;

import org.mosspaper.Env;
import org.mosspaper.Layout;
import org.mosspaper.Parser.NewLine;

public abstract class AbsAlign implements MossObject {

    public void preDraw(Env env) { }

    protected Env buildEnv(Env env) { 
        Env tmpEnv = env.dummyClone();
        /* Draw from here to end of line */
        Layout objs = env.getLayout();
        if (objs == null) {
            return null;
        }
        int start = objs.indexOf(this);
        for (int i = start;; i++) {
            MossObject m = objs.get(i);
            if (m instanceof NewLine) {
                break;
            }
            m.draw(tmpEnv);
        }
        return tmpEnv;
    }

    public void postDraw(Env env) { }

    public DataProvider getDataProvider() {
        return null;
    }

}
