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

import java.io.File;
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

        Preference imagePref = (Preference) findPreference("background_image");
        if (null != imagePref) {
            imagePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {
                    /* TODO: what's a better way todo this? */
                    mTempFile = new File("/sdcard/moss-background.png");
                    mTempFile.getParentFile().mkdirs();

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setType("image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("outputX", Env.Current.INSTANCE.env.getPaperWidth());
                    intent.putExtra("outputY", Env.Current.INSTANCE.env.getPaperHeight());
                    intent.putExtra("aspectX", Env.Current.INSTANCE.env.getPaperWidth());
                    intent.putExtra("aspectY", Env.Current.INSTANCE.env.getPaperHeight());
                    intent.putExtra("scale", true);
                    intent.putExtra("noFaceDetection", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.name());
                    MossSettings.this.startActivityForResult(intent, SELECT_IMAGE);

                    return true;
                }
            });
        }

        Preference reload = (Preference) findPreference("config_reload");
        if (null != reload) {
            reload.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {
                    reloadConfig(prefs, "config_reload");
                    return true;
                }
            });
        }
        Preference reset = (Preference) findPreference("config_reset");
        if (null != reset) {
            reset.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {
                    resetDefaults();
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

        defaultPrefs();
        updatePrefs();
        onSharedPreferenceChanged(prefs, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Preference pref = (Preference) findPreference("background_image");
            pref.setSummary(mTempFile.toString());

            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(pref.getKey(), mTempFile.toString());
            edit.commit();
        }
    }

    @Override
    protected void onResume() {
        updatePrefs();
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
        updatePrefs();

        if ("sample_config_file".equals(key) || null == key) {
            Preference pref = findPreference("config_file");
            if (Config.CUSTOM.equals(sharedPreferences.getString("sample_config_file", ""))) {
                pref.setEnabled(true);
            } else {
                pref.setEnabled(false);
            }
        }
        if ("sample_config_file".equals(key) || "config_file".equals(key)) {
            reloadConfig(prefs, key);
            checkErrors();
        }
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
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info =
            (AdapterView.AdapterContextMenuInfo) menuInfo;

        final Preference pref = (Preference) this.getListView().getItemAtPosition(info.position);;
        menu.setHeaderTitle(pref.getTitle());

        
        if (!isDefaultable(pref.getKey())) {
            return;
        }

        MenuItem reset = menu.add(R.string.reset);
        reset.setEnabled(true);
        reset.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.remove(pref.getKey());
                edit.commit();

                defaultPrefs();

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
        final Preference r = MossSettings.this.findPreference(key);
        final CharSequence sum = r.getSummary();
        r.setEnabled(false);
        r.setSummary(R.string.loading);
        new Thread(new Runnable() {
            public void run() {
                Env.reload(MossSettings.this, prefs);
                MossSettings.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (null != r) {
                            r.setSummary(sum);
                            r.setEnabled(true);
                        }
                    }
                });
            }
        }).start();
    }

    private void checkErrors() {
        boolean hasErrors = false;
        synchronized (currentEnv) {
            /* XXX: buzz, your girlfriend...wooof, I can do better then this */
            if (currentEnv.env != null && currentEnv.env.hasExs()) {
                hasErrors = true;
            }
        }
        if (hasErrors) {
            showDialog(DIA_ERROR_LIST);
        }
    }

    private void defaultPrefs() {
        Env.Current singleton = Env.Current.INSTANCE;
        if (null == singleton.env) {
            return;
        }
        SharedPreferences.Editor edit = prefs.edit();
        if (-1.0f == prefs.getFloat("font_size", -1.0f)) {
            edit.putFloat("font_size", singleton.env.getConfig().getFontSize());
        }
        if (-1 == prefs.getInt("background_color", -1)) {
            int c = singleton.env.getConfig().getBackgroundColor();
            if (-1 != c) {
                c |= 0xFF000000;
                edit.putInt("background_color", c);
            }
        }
        if (-1 == prefs.getInt("mod_color", -1)) {
            int c = singleton.env.getConfig().getModColor();
            if (-1 != c) {
                c |= 0xFF000000;
                edit.putInt("mod_color", c);
            }
        }
        edit.commit();
    }

    protected void updatePrefs() {
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        for (String key : prefs.getAll().keySet()) {
            Preference p = this.findPreference(key);

            if (p == null) {
                continue;
            }

            if (p instanceof CheckBoxPreference 
                    || p instanceof ListPreference) {
                continue;
            }
            CharSequence value;
            if ("font_size".equals(key)) {
                value = String.format("%.0f", prefs.getFloat(key, -1.0f));
            } else if ("background_color".equals(key) || "mod_color".equals(key)) {
                value = String.format("#%x", prefs.getInt(key, 0));
            } else {
                value = prefs.getString(key, "");
            }
            if (null != value) {
                p.setSummary(value);
            }
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

    private void resetDefaults() {
        SharedPreferences.Editor edit = prefs.edit();
        for (String k : defaultable) {
            edit.putString(k, null);
        }
        edit.commit();
        defaultPrefs();
    }

    private boolean isDefaultable(String key) {
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
    private File mTempFile;
    private LayoutInflater inflater = null;
    static final int SELECT_IMAGE = 1111;
}
