package org.mosspaper.objects;

import org.mosspaper.Common;
import org.mosspaper.Env;
import org.mosspaper.ParseException;

public class Entropy {

    public static class PoolSize extends AbsMossObject implements MossObject {

        /**
        * Display entropy pool size
        */
        public PoolSize() { }

        public DataProvider getDataProvider() {
            return entropy;
        }

        @Override
        public String toString() {
            return Common.humanReadble(entropy.getPoolSize());
        }

        private ProcEntropy entropy = ProcEntropy.INSTANCE;
    }

    public static class Available extends AbsMossObject implements MossObject {

        /**
        * Display entropy available
        */
        public Available() { }

        public DataProvider getDataProvider() {
            return entropy;
        }

        @Override
        public String toString() {
            return Common.humanReadble(entropy.getAvailable());
        }

        private ProcEntropy entropy = ProcEntropy.INSTANCE;
    }

    public static class Percent extends AbsMossObject implements MossObject {

        /**
        * Display percent of entropy available
        */
        public Percent() { }

        public DataProvider getDataProvider() {
            return entropy;
        }

        @Override
        public String toString() {
            return String.format("%.2f%%", entropy.getPerc() * 100.0f);
        }

        private ProcEntropy entropy = ProcEntropy.INSTANCE;
    }

    public static class Bar extends AbsBarObject  implements MossObject {

        /**
        * Display a bar of the entropy avail / poolsize
        */
        public Bar(String hw) throws ParseException {
            super(hw);
        }

        public DataProvider getDataProvider() {
            return battInfo;
        }

        public void preDraw(Env env) { }

        public void draw(Env env) {
            doDraw(env, battInfo.getPerc());
        }

        public void postDraw(Env env) { }

        private ProcEntropy battInfo = ProcEntropy.INSTANCE;
    }

}
