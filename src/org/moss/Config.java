package org.moss;

import org.moss.objects.Color;

public class Config {

    public static enum HAlign {
        LEFT, MIDDLE, RIGHT
    }

    public static enum VAlign {
        TOP, MIDDLE, BOTTOM
    };

    public Config() {
        this.autoReload = true;
        updateInterval = CONF_UPDATE_INTERVAL_VALUE;
        fontSize = CONF_FONT_SIZE_VALUE;
    }

    public void put(String k, String v) throws ConfigException {
        if (k.indexOf("color") == 0) {
            String cname = k.replaceAll("color", "");
            int cvalue = Common.hexToInt(v, -1);
            if (cvalue > -1) {
                Color.colorMap.put(cname, cvalue);
            }
        } else if (CONF_UPDATE_INTERVAL.equals(k)) {
            try {
                updateInterval = new Float(v).floatValue();
            } catch (NumberFormatException e) {
                updateInterval = CONF_UPDATE_INTERVAL_VALUE;
                throw new ConfigException(v + " is invalid.");
            }
        } else if (CONF_FONT_SIZE.equals(k)) {
            try {
                fontSize = new Float(v).floatValue();
            } catch (NumberFormatException e) {
                fontSize = CONF_FONT_SIZE_VALUE;
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
            String[] colors = v.split(" ");
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
        } else if ("alignment".equals(k)) {
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
        return fontSize;
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

    private VAlign valign;
    private HAlign halign;
    private boolean autoReload;
    private float gapX;
    private float gapY;
    private float updateInterval;
    private float fontSize;
    private String backgroundColor;
    private String modColor;
    private String defaultColor;
    private String outlineColor;
    private String shadeColor;


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
     * Set the default color of the paint.
     */
    public static final String CONF_DEFAULT_COLOR = "default_color";

    /**
     * Determines how frequently the data, and display are updated.
     */
    public static final String CONF_UPDATE_INTERVAL = "update_interval";
    public static final float CONF_UPDATE_INTERVAL_VALUE = 1.0f;

    /**
     * Sets the font size for the display.
     */
    public static final String CONF_FONT_SIZE = "font_size";
    public static final float CONF_FONT_SIZE_VALUE = 12.0f;

    /**
     * An absolute path to a background image. <code>/sdcard/moss/bg-fav.png</code>
     */
    // public static final String CONF_BACKGROUND_IMAGE = background_image;

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

    public static final String CUSTOM = "CUSTOM";
}

