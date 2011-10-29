package org.moss;

import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;

import org.moss.prefs.PrefUtils;

public class OverridesActivity extends PreferenceActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getPreferenceManager().setSharedPreferencesName(WallPaper.SHARED_PREFS_NAME);
        addPreferencesFromResource(R.xml.prefs_overrides);

        prefs = getPreferenceManager().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);

        this.registerForContextMenu(this.getListView());
        this.inflater = LayoutInflater.from(this);

        Preference reset = (Preference) findPreference("config_reset");
        if (null != reset) {
            reset.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {
                    PrefUtils.resetPrefs(currentEnv.env, prefs);

                    return true;
                }
            });
        }

        onSharedPreferenceChanged(prefs, null);
    }

    @Override
    protected void onResume() {
        PrefUtils.updatePrefs(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PrefUtils.updatePrefs(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, final View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info =
            (AdapterView.AdapterContextMenuInfo) menuInfo;

        final Preference pref = (Preference) this.getListView().getItemAtPosition(info.position);;
        menu.setHeaderTitle(pref.getTitle());


        if (!PrefUtils.isDefaultable(pref.getKey())) {
            return;
        }

        MenuItem reset = menu.add(R.string.reset);
        reset.setEnabled(true);
        reset.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.remove(pref.getKey());
                edit.commit();

                PrefUtils.defaultPrefs(currentEnv.env, prefs);
                PrefUtils.updatePrefs(OverridesActivity.this);

                return true;
            }
        });
    }

    private Env.Current currentEnv = Env.Current.INSTANCE;

    static final String TAG = "OverridesActivity";

    private SharedPreferences prefs;
    private LayoutInflater inflater = null;
    static final int SELECT_CONFIG = 1111;
}
