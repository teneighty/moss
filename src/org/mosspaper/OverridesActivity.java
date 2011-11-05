package org.mosspaper;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import org.mosspaper.prefs.PrefUtils;

import java.io.File;

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
                    PrefUtils.defaultPrefs(currentEnv.env, prefs);

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
        if ("config_file".equals(key)) {
            Preference pref = this.findPreference(key);
            if (null == prefs.getString(key, null)) {
                pref.setSummary(getString(R.string.path_to_config));
            } else {
                File file = new File(prefs.getString(key, ""));
                if (file.exists()) {
                    pref.setSummary(file.toString());
                    startReloadTask();
                } else {
                    pref.setSummary(getString(R.string.does_not_exist, file.toString()));
                }
            }
        } else {
            PrefUtils.updatePrefs(this);
        }
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

    private void startReloadTask() {
        pdialog = new ProgressDialog(this);
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setMessage(getString(R.string.reloading));
        pdialog.show();

        new ReloadTask().execute();
    }

    class ReloadTask extends AsyncTask<String, String, Long> {

        protected Long doInBackground(String... s) {
            Env.reload(OverridesActivity.this);

            return 0L;
        }

        protected void onPostExecute(Long result) {
            OverridesActivity.this.pdialog.dismiss();
        }
    }

    private Env.Current currentEnv = Env.Current.INSTANCE;
    private ProgressDialog pdialog;
    private SharedPreferences prefs;
    private LayoutInflater inflater = null;

    static final String TAG = "OverridesActivity";
}
