package org.mosspaper.objects;

import android.graphics.Typeface;

import org.mosspaper.Env;
import org.mosspaper.Config;
import org.mosspaper.ParseException;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

public class Font implements MossObject {

    private FontInfo mFontInfo;

    static class FontInfo {
        String family;
        int size;
        int style;

        @Override
        public String toString() {
            return family + ":" + size;
        }
    }

    /**
     * Reset to the current font.
     */
    public Font() {
        this.mFontInfo = null;
    }

    /**
     * Change to the font described in font
     *
     * @param font The font to switch to for drawing
     */
    public Font(String font) throws ParseException {
        this();
        if (null != fontMap.get(font)) {
            this.mFontInfo = fontMap.get(font);
        } else {
            this.mFontInfo = parseFont(font);
        }
        if (ttfMap.get(mFontInfo.family) == null) {
            Typeface t = Typeface.create(mFontInfo.family, mFontInfo.style);
            ttfMap.put(mFontInfo.family, t);
        }
    }

    public void preDraw(Env env) { }

    public void draw(Env env) {
        if (mFontInfo == null) {
            env.getPaint().setTextSize(env.getFontSize());
            env.getPaint().setTypeface(Typeface.MONOSPACE);
        } else {
            Typeface t = ttfMap.get(mFontInfo.family);
            if (t != null) {
                env.getPaint().setTypeface(t);
            }
            env.getPaint().setTextSize(mFontInfo.size);
        }
        env.setLineHeight(env.getPaint().getTextSize());
    }

    public void postDraw(Env env) { }

    public DataProvider getDataProvider() {
        return null;
    }

    public static int getDefaultFontSize() {
        return fontMap.get("default").size;
    }

    public static void loadFont(String alias, String font) throws ParseException {
        fontMap.put(alias, parseFont(font));
    }

    public static void addFontObject(String alias, String font) throws ParseException {
        TextObjects.TEXT_OBJECTS.put(alias, new TextObjects.FontArgs(font));
    }

    public static void loadTypeface(Env env, String family, String path) throws ParseException {
        try {
            Typeface t = null;
            File f = new File(path);
            if (f.isAbsolute()) {
                t = Typeface.createFromFile(path);
            } else {
                if (null != env && env.getConfigFile() != null) {
                    File parentFile = env.getConfigFile().getParentFile();
                    if (parentFile != null) {
                        File fullpath = new File(parentFile, path);
                        t = Typeface.createFromFile(fullpath.toString());
                    }
                }
            }
            if (t != null) {
                ttfMap.put(family, t);
            } else {
                throw new ParseException("Unable to load " + path);
            }
        } catch (RuntimeException e) {
            throw new ParseException("Unable to load " + path);
        }
    }

    static FontInfo parseFont(String font) throws ParseException {

        FontInfo f = new FontInfo();
        String[] arr = font.split(":");
        f.family = arr[0];

        try {
            f.size = new Integer(font.replaceAll(".*size=(\\d+).*", "$1"));
        } catch (NumberFormatException e) {
            throw new ParseException("Unable to parse font!");
        }
        parseStyle(f, font);

        return f;
    }

    static void parseStyle(FontInfo fi, String font) {
        String style = null == font ? "" : font.toLowerCase();
        if (style.contains("bold")) {
            fi.style = Typeface.BOLD;
        } else if (style.contains("bold_italic")) {
            fi.style = Typeface.BOLD_ITALIC;
        } else if (style.contains("bold_italic")) {
            fi.style = Typeface.BOLD_ITALIC;
        } else {
            fi.style = Typeface.NORMAL;
        }
    }

    public static Map<String, Typeface> ttfMap;
    public static Map<String, FontInfo> fontMap;

    static {
        FontInfo fi = new FontInfo();
        fi.family = "Droid Sans Mono";
        fi.size = (int) Config.CONF_FONT_SIZE_VALUE;

        fontMap = new HashMap<String, FontInfo>();
        fontMap.put("default", fi);

        ttfMap = new HashMap<String, Typeface>();
    }
}
