package org.moss;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView;

import org.moss.prefs.PrefUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class MossSettings extends PreferenceActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getPreferenceManager().setSharedPreferencesName(MossPaper.SHARED_PREFS_NAME);
        addPreferencesFromResource(R.layout.act_settings);

        prefs = getPreferenceManager().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);

        this.registerForContextMenu(this.getListView());
        this.inflater = LayoutInflater.from(this);

        Preference configList = (Preference) findPreference("config_list");
        if (null != configList) {
            configList.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {
                    startActivity(new Intent(MossSettings.this, MossConfigs.class));
                    return true;
                }
            });
        }

        Preference overrides = (Preference) findPreference("config_overrides");
        if (null != overrides) {
            overrides.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {
                    startActivity(new Intent(MossSettings.this, MossOverrides.class));
                    return true;
                }
            });
        }

        Preference reload = (Preference) findPreference("config_reload");
        if (null != reload) {
            reload.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {
                    reloadConfig(prefs, "config_reload");
                    PrefUtils.updatePrefs(MossSettings.this);
                    checkErrors();
                    return true;
                }
            });
        }
        Preference help = (Preference) findPreference("config_help");
        if (null != help) {
            help.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {
                    startActivity(new Intent(MossSettings.this, MossHelp.class));
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

        if ("sample_config_file".equals(key) || "config_file".equals(key)) {
            reloadConfig(prefs, key);
            checkErrors();
        }
        PrefUtils.updatePrefs(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.help:
            Intent helpIntent = new Intent(this, MossHelp.class);
            startActivity(helpIntent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
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
                PrefUtils.updatePrefs(MossSettings.this);

                return true;
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch(id) {
        case DIA_ERROR_LIST:
            dialog = getErrorDialog();
            break;
        default:
            dialog = null;
        }
        return dialog;
    }

    private void reloadConfig(final SharedPreferences prefs, String key) {
        new Thread(new Runnable() {
            public void run() {
                Env.reload(MossSettings.this);
            }
        }).start();
    }

    private void checkErrors() {
        boolean hasErrors = false;
        synchronized (currentEnv) {
            /* TODO: buzz, your girlfriend...wooof, I can do better then this */
            if (currentEnv.env != null && currentEnv.env.hasExs()) {
                hasErrors = true;
            }
        }
        if (hasErrors) {
            showDialog(DIA_ERROR_LIST);
        }
    }

    private Dialog getErrorDialog() {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;

        Context context = MossSettings.this;
        View layout = inflater.inflate(R.layout.dia_errors,
                                    (ViewGroup) findViewById(R.id.layout_root));

        ListView list = (ListView) layout.findViewById(R.id.error_list);
        list.setAdapter(new ErrorAdapter(context, currentEnv.env.getExs()));

        builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.setTitle(R.string.dia_errors_title);

        return alertDialog;
    }

    private Env.Current currentEnv = Env.Current.INSTANCE;

    static final int DIA_ERROR_LIST = 1;
    static final String TAG = "MossSettings";

    class ErrorAdapter extends ArrayAdapter<MossException> {
        public ErrorAdapter(Context context, List<MossException> exs) {
            super(context, R.layout.item_device, exs);
            this.exs = exs;
        }

        @Override
        public View getView(int position,
                            View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView =
                    inflater.inflate(R.layout.item_device, null, false);

                holder = new ViewHolder();
                holder.brief =
                    (TextView) convertView.findViewById(android.R.id.text1);
                holder.summary =
                    (TextView) convertView.findViewById(android.R.id.text2);
                holder.icon =
                    (ImageView) convertView.findViewById(android.R.id.icon);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MossException ex = exs.get(position);
            if (ex == null) {
                Log.e("ErrorAdapter", "Exception is null!");
                holder.brief.setText("Error during lookup");
                return convertView;
            }

            holder.brief.setText(ex.getBrief());
            holder.summary.setText(ex.getSummary());

            switch (ex.getErrType()) {
            case MossException.ERROR:
                holder.icon.setImageState(
                    new int[] {android.R.attr.state_expanded}, true);
                break;
            case MossException.WARNING:
                holder.icon.setImageState(new int[] {}, true);
                break;
            default:
                holder.icon.setImageState(new int[] {}, true);
                break;
            }

            Context context = convertView.getContext();
            holder.brief.setTextAppearance(context,
                    android.R.attr.textAppearanceLarge);
            holder.summary.setTextAppearance(context,
                    android.R.attr.textAppearanceSmall);

            return convertView;
        }

        class ViewHolder {
            private TextView brief;
            private TextView summary;
            private ImageView icon;
        }

        private List<MossException> exs;
    }

    private SharedPreferences prefs;
    private LayoutInflater inflater = null;
    static final int SELECT_CONFIG = 1111;
}
