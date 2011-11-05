package org.mosspaper.objects;

import org.mosspaper.Env;
import org.mosspaper.ParseException;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.DashPathEffect;

public class StippledHRule extends HRule implements MossObject {

    /**
     * Draw a stippled horizontal rule with the default spacing.
     */
    public StippledHRule() {
        super();
        space = DEF_SPACE;
    }

    /**
     * Draw a stippled horizontal rule with the default spacing.
     *
     * @param spacing a number to specify the size of the dashes.
     */
    public StippledHRule(String spacing) throws ParseException {
        super();
        try {
            space = new Float(spacing).floatValue();
        } catch (NumberFormatException e) {
            throw new ParseException(String.format("Invalid spacing: '%s'", spacing));
        }
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        final Paint p = env.getPaint();
        Style s = p.getStyle();
        p.setStyle(Paint.Style.STROKE);
        p.setPathEffect(
            new DashPathEffect(new float[] {space, space}, 0));

        env.getCanvas().drawLine(
            env.getX(), env.getY(),
            env.getMaxX(), env.getY(),
            env.getPaint());
            env.setY(env.getY() + env.getPaint().getTextSize());

        p.setPathEffect(null);
        p.setStyle(s);
    }

    public void postDraw(Env env) { }

    private float space;

    static final float DEF_SPACE = 10f;
}
