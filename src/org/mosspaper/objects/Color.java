package org.mosspaper.objects;

import org.mosspaper.Env;
import org.mosspaper.Common;
import org.mosspaper.ParseException;

import java.util.Map;
import java.util.HashMap;

public class Color implements MossObject {

    /**
     * Change the current paint color to the default color.
     */
    public Color() { }

    /**
     * Change the current paint color to the value of color.
     *
     * @param color a color name or hexadecimal value
     */
    public Color(String color) throws ParseException {
        this.color = lookupColor(color);
        if (this.color < 0) {
            throw new ParseException(String.format("Invalid color format: '%s'", color));
        }
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        int c = -1;
        if (null == this.color || this.color < 0) {
            c = env.getConfig().getDefaultColor();
        } else {
            c = this.color;
        }
        if (c > -1) {
            env.getPaint().setColor(c);
            env.getPaint().setAlpha(0xFF);
        }
    }

    public void postDraw(Env env) { }

    public DataProvider getDataProvider() {
        return null;
    }

    public static int lookupColor(String c) {
        Integer color = colorMap.get(c);
        if (null == color) {
            color = Common.hexToInt(c, -1);
        }
        return color;
    }

    public static void addColorObject(String alias, String color) {
        TextObjects.TEXT_OBJECTS.put(alias, new TextObjects.ColorArgs(color));
    }

    private Integer color;

    public static Map<String, Integer> colorMap;
    static {
        colorMap = new HashMap<String, Integer>();
        colorMap.put("red", 0xff0000);
        colorMap.put("green", 0x00ff00);
        colorMap.put("yellow", 0xffff00);
        colorMap.put("blue", 0x0000ff);
        colorMap.put("magenta", 0x800080);
        colorMap.put("cyan", 0x00ffff);
        colorMap.put("black", 0x000000);
        colorMap.put("white", 0xffffff);
        colorMap.put("grey", 0x808080);
        colorMap.put("lightgrey", 0xc0c0c0);
    }
}
