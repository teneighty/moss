package org.mosspaper;

import org.mosspaper.objects.Color;
import org.mosspaper.objects.Font;

import java.io.File;

public class Config {

    public static enum HAlign {
        LEFT, MIDDLE, RIGHT
    }

    public static enum VAlign {
        TOP, MIDDLE, BOTTOM
    };

    public Config() {
        this.autoReload = true;
        this.updateInterval = CONF_UPDATE_INTERVAL_VALUE;
        this.halign = HAlign.LEFT;
        this.valign = VAlign.TOP;
    }

    public void put(Env env, String k, String v) throws MossException {
        if (CONF_UPDATE_INTERVAL.equals(k)) {
            try {
                updateInterval = new Float(v).floatValue();
            } catch (NumberFormatException e) {
                updateInterval = CONF_UPDATE_INTERVAL_VALUE;
                throw new ConfigException(v + " is invalid.");
            }
        } else if (CONF_BACKGROUND_COLOR.equals(k)) {
            backgroundColor = v;
        } else if (CONF_DEFAULT_COLOR.equals(k)) {
            defaultColor = v;
        } else if (CONF_DEFAULT_OUTLINE_COLOR.equals(k)) {
            outlineColor = v;
        } else if (CONF_DEFAULT_SHADE_COLOR.equals(k)) {
            shadeColor = v;
        } else if (CONF_BACKGROUND_MOD.equals(k)) {
            String[] colors = v.split("\\s+");
            if (colors.length == 2) {
                backgroundColor = colors[0];
                modColor = colors[1];
            } else {
                throw new ConfigException(v + " is invalid.");
            }
        } else if (CONF_DISABLE_AUTO_RELOAD.equals(k)) {
            if ("yes".equals(v)) {
                autoReload = false;
            } else if ("no".equals(v)) {
                autoReload = true;
            } else {
                throw new ConfigException(v + " is invalid. yes or no.");
            }
        } else if (CONF_GAP_X.equals(k)) {
            try {
                gapX = new Float(v).floatValue();
            } catch (NumberFormatException e) {
                gapX = 0.0f;
                throw new ConfigException(v + " is invalid.");
            }
        } else if (CONF_GAP_Y.equals(k)) {
            try {
                gapY = new Float(v).floatValue();
            } catch (NumberFormatException e) {
                gapY = 0.0f;
                throw new ConfigException(v + " is invalid.");
            }
        } else if (CONF_BACKGROUND_IMAGE.equals(k)) {
            File img = new File(v);
            if (img != null
                    && !img.isAbsolute()
                    && null != env.getConfigFile()) {
                File pfile = env.getConfigFile().getParentFile();
                img = new File(pfile, v);
            }
            if (img == null) {
                throw new ConfigException("Invalid background file");
            } else {
                bgImage = img.toString();
            }
        } else if (CONF_DEFAULT_UPPERCASE.equals(k)) {
            if ("yes".equals(v.trim())) {
                uppercase = true;
            } else if ("no".equals(v.trim())) {
                uppercase = false;
            } else {
                throw new ConfigException("Invalid type for uppercase.");
            }
        } else if (CONF_ALIGNMENT.equals(k)) {
            String[] split = v.split("_");
            if (split.length == 2) {
                if ("top".equals(split[0])) {
                    valign = VAlign.TOP;
                } else if ("middle".equals(split[0])) {
                    valign = VAlign.MIDDLE;
                } else if ("bottom".equals(split[0])) {
                    valign = VAlign.BOTTOM;
                } else {
                    throw new ConfigException(v + " is invalid.");
                }
                if ("left".equals(split[1])) {
                    halign = HAlign.LEFT;
                } else if ("middle".equals(split[1])) {
                    halign = HAlign.MIDDLE;
                } else if ("right".equals(split[1])) {
                    halign = HAlign.RIGHT;
                } else {
                    throw new ConfigException(v + " is invalid.");
                }
            } else {
                throw new ConfigException(v + " is invalid.");
            }
        } else if (CONF_ADD_COLOR.equals(k)) {
            String[] arr = v.split("\\s+");
            if (arr.length == 2) {
                int cvalue = Common.hexToInt(arr[1].trim(), -1);
                if (cvalue > -1) {
                    Color.colorMap.put(arr[0].trim(), cvalue);
                }
            } else {
                throw new ConfigException("'" + v + "' is invalid.");
            }
        } else if (k.indexOf("color") == 0) {
            Color.addColorObject(k, v);
        } else if (CONF_FONT.equals(k)) {
            Font.loadFont("default", v);
        } else if ("font_load".equals(k)) {
            String[] arr = v.split("\\s+");
            if (arr.length == 2) {
                Font.loadTypeface(env, arr[0], arr[1]);
            } else {
                throw new ConfigException("requires alias and font path.");
            }
        } else if (k.indexOf("font") == 0) {
            Font.addFontObject(k, v);
        } else {
            throw new ConfigException("Unknown config option.");
        }
    }

    public VAlign getVAlign() {
        return valign;
    }

    public HAlign getHAlign() {
        return halign;
    }

    public float getGapX() {
        return gapX;
    }

    public float getGapY() {
        return gapY;
    }

    public float getUpdateInterval() {
        return updateInterval;
    }

    public float getFontSize() {
        return (float) Font.getDefaultFontSize();
    }

    public int getModColor() {
        return Color.lookupColor(modColor);
    }

    public int getBackgroundColor() {
        return Color.lookupColor(backgroundColor);
    }

    public int getDefaultColor() {
        return Color.lookupColor(defaultColor);
    }

    public int getOutlineColor() {
        return Color.lookupColor(outlineColor);
    }

    public int getShadeColor() {
        return Color.lookupColor(shadeColor);
    }

    public boolean getAutoReload() {
        return autoReload;
    }

    public String getBackgroundImagePath() {
        return bgImage;
    }

    public boolean getUppercase() {
        return uppercase;
    }

    private VAlign valign;
    private HAlign halign;
    private boolean autoReload;
    private boolean uppercase;
    private float gapX;
    private float gapY;
    private float updateInterval;
    private String backgroundColor;
    private String modColor;
    private String defaultColor;
    private String outlineColor;
    private String shadeColor;
    private String bgImage;


    /**
     * Additional horizonal spacing
     */
    public static final String CONF_GAP_X = "gap_x";

    /**
     * Additional vertical spacing
     */
    public static final String CONF_GAP_Y = "gap_y";

    /**
     * Enable to disable file observer (inotify) based auto config reload.
     */
    public static final String CONF_DISABLE_AUTO_RELOAD = "disable_auto_reload";

    /**
     * Added or override colors to color dictionary
     */
    public static final String CONF_ADD_COLOR = "color_add";
    
    /**
     * Set the alignment the layout 
     */
    public static final String CONF_ALIGNMENT = "alignment";

    /**
     * Set the default color of the paint.
     */
    public static final String CONF_DEFAULT_COLOR = "default_color";

    /**
     * Determines how frequently the data, and display are updated.
     */
    public static final String CONF_UPDATE_INTERVAL = "update_interval";
    public static final float CONF_UPDATE_INTERVAL_VALUE = 1.0f;

    /**
     * Sets the default font for the display.
     */
    public static final String CONF_FONT = "font";
    public static final float CONF_FONT_SIZE_VALUE = 12.0f;

    /**
     * An absolute path to a background image. <code>/sdcard/moss/bg-fav.png</code>
     */
    public static final String CONF_BACKGROUND_IMAGE = "background_image";

    /**
     * The background color for the wallpaper.
     */
    public static final String CONF_BACKGROUND_COLOR = "background_color";
    public static final int CONF_BACKGROUND_COLOR_VALUE = 0xff000000;

    /**
     * Like xsetroot's mod parameter, allows the user to create a plaid like
     * pattern.
     */
    public static final String CONF_BACKGROUND_MOD = "background_mod";

    /**
     * default outline color.
     */
    public static final String CONF_DEFAULT_OUTLINE_COLOR = "default_outline_color";

    /**
     * default shade color.
     */
    public static final String CONF_DEFAULT_SHADE_COLOR = "default_shade_color";

    /**
     * All text in uppercase
     */
    public static final String CONF_DEFAULT_UPPERCASE = "uppercase";

    public static final String CUSTOM = "CUSTOM";
}

