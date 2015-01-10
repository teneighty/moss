package org.mosspaper.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.Preference;

import org.mosspaper.Common;
import org.mosspaper.Env;

public class PrefUtils {

    public static void updatePrefs(PreferenceActivity activity) {
        SharedPreferences prefs = activity.getPreferenceManager().getSharedPreferences();
        for (String key : prefs.getAll().keySet()) {
            Preference p = activity.findPreference(key);

            if (p == null) {
                continue;
            }

            if (p instanceof CheckBoxPreference
                    || p instanceof ListPreference) {
                continue;
            }
            CharSequence value = null;
            if ("font_size".equals(key) || "gap_x".equals(key) || "gap_y".equals(key)) {
                value = String.format("%.0f", prefs.getFloat(key, -1.0f));
            } else if ("update_interval".equals(key)) {
                value = Common.formatSecondsShort((long) prefs.getFloat(key, -1.0f));
            } else if ("background_color".equals(key) || "mod_color".equals(key)) {
                int c = prefs.getInt(key, -1);
                if (-1 != c) {
                    value = String.format("#%x", c);
                } else {
                    value = "No color";
                }
            } else {
                value = prefs.getString(key, "");
            }
            if (null != value) {
                p.setSummary(value);
            }
        }
    }

    public static void resetPrefs(Env env, SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit();
        for (String k : defaultable) {
            edit.remove(k);
        }
        edit.commit();
    }

    public static void defaultPrefs(Env env, SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit();
        if (-1.0f == prefs.getFloat("update_interval", -1.0f)) {
            edit.putFloat("update_interval", env.getConfig().getUpdateInterval());
        }
        if (-1.0f == prefs.getFloat("font_size", -1.0f)) {
            edit.putFloat("font_size", env.getConfig().getFontSize());
        }
        if (-1.0f == prefs.getFloat("gap_x", -1.0f)) {
            edit.putFloat("gap_x", env.getGapX());
        }
        if (-1.0f == prefs.getFloat("gap_y", -1.0f)) {
            edit.putFloat("gap_y", env.getGapY());
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
        "background_image", 
        "background_color", "mod_color", "font_size"
    };
}
