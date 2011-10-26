package org.moss.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import org.moss.Env;

public class PrefUtils {

    public static void resetPrefs(Env env, SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit();
        for (String k : defaultable) {
            edit.remove(k);
        }
        edit.commit();
    }

    public static void defaultPrefs(Env env, SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit();
        if (-1.0f == prefs.getFloat("font_size", -1.0f)) {
            edit.putFloat("font_size", env.getConfig().getFontSize());
        }
        if (-1 == prefs.getInt("background_color", -1)) {
            int c = env.getConfig().getBackgroundColor();
            if (-1 != c) {
                c |= 0xFF000000;
                edit.putInt("background_color", c);
            }
        }
        if (-1 == prefs.getInt("mod_color", -1)) {
            int c = env.getConfig().getModColor();
            if (-1 != c) {
                c |= 0xFF000000;
                edit.putInt("mod_color", c);
            }
        }
        edit.commit();
    }


    public static boolean isDefaultable(String key) {
        for (String k : defaultable) {
            if (k.equals(key)) {
                return true;
            }
        }
        return false;
    }

    static String[] defaultable = new String[] {
        "background_image", "background_color", "mod_color", "font_size"
    };
}
