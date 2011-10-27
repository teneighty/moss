package org.moss;

import android.app.Activity;
import android.app.ListActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.ArrayList;

public class Configurations extends ListActivity  {

    static final String TAG = "Configurations";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.act_configs);
        this.registerForContextMenu(this.getListView());

        handleIntents();
        updateList();

        this.inflater = LayoutInflater.from(this);
        this.setListAdapter(
                new ConfigAdapter(this, configs));

        ListView list = this.getListView();
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v,
                                    int position, long id) {
                if (position >= configs.size()) {
                    /* TODO: report error */
                    return;
                }
                ConfigDatabase.Config c = configs.get(position);

                pdialog = createDialog();
                pdialog.setMessage(Configurations.this.getString(R.string.switching_to, c.name));
                pdialog.show();

                new ReloadTask().execute(c);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHandler.sendEmptyMessage(-1);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, final View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info =
            (AdapterView.AdapterContextMenuInfo) menuInfo;

        final ConfigDatabase.Config config =
            (ConfigDatabase.Config) this.getListView().getItemAtPosition(info.position);;

        MenuItem remove = menu.add(R.string.remove);
        remove.setEnabled(!config.asset);
        remove.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                pdialog = createDialog();
                pdialog.show();

                new RemoveTask().execute(config);

                return true;
            }
        });
    }

    private void handleIntents() {
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri data = intent.getData();
            android.util.Log.i(TAG, data.toString());

            pdialog = createDialog();
            pdialog.show();

            new PackLoaderTask().execute(data);
        }
    }

    private void updateList() {
        ConfigDatabase db = new ConfigDatabase(this);
        configs = db.getConfigs();
        db.close();

        this.setListAdapter(
                new ConfigAdapter(this, configs));

    }

    protected Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Configurations.this.updateList();
        }
    };

    private ProgressDialog createDialog() {
        pdialog = new ProgressDialog(this);
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setMessage(getString(R.string.loading));
        pdialog.setCancelable(false);
        return pdialog;
    }

    class ReloadTask extends AsyncTask<ConfigDatabase.Config, String, Long> {

        protected Long doInBackground(ConfigDatabase.Config... configs) {
            ConfigDatabase.Config c = configs[0];

            SharedPreferences prefs =
                Configurations.this.getSharedPreferences(WallPaper.SHARED_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("config_list", c.name);
            if (c.asset) {
                edit.putString("sample_config_file", c.filepath);
                edit.putString("config_file", null);
            } else {
                edit.putString("sample_config_file", "CUSTOM");
                edit.putString("config_file", c.filepath);
            }
            edit.commit();
            Env.reload(Configurations.this);
            updateHandler.sendEmptyMessage(-1);

            return 0L;
        }

        protected void onPostExecute(Long result) {
            Configurations.this.pdialog.dismiss();
        }
    }

    class RemoveTask extends AsyncTask<ConfigDatabase.Config, String, Long> {

        protected void onProgressUpdate(String... progress) {
            pdialog.setMessage(progress[0]);
        }

        protected Long doInBackground(ConfigDatabase.Config... configs) {
            ConfigDatabase.Config c = configs[0];

            publishProgress(
                Configurations.this.getString(R.string.removing_from_db, c.name));
            ConfigDatabase db = new ConfigDatabase(Configurations.this);
            db.deleteConfig(c);
            db.close();

            File confFile = new File(c.filepath);
            if (confFile.exists()) {
                File confDir = confFile.getParentFile();
                publishProgress(
                    Configurations.this.getString(R.string.removing_from_fs, confDir.toString()));
                recursiveDelete(confDir);
            }

            updateHandler.sendEmptyMessage(-1);

            return 0L;
        }

        private void recursiveDelete(File fs) {
            if (null == fs || !fs.exists()) {
                return;
            }
            for (File f : fs.listFiles()) {
                if (f.isDirectory()) {
                    recursiveDelete(f);
                } else {
                    f.delete();
                }
            }
            fs.delete();
        }

        protected void onPostExecute(Long result) {
            Configurations.this.pdialog.dismiss();
        }
    }

    class PackLoaderTask extends AsyncTask<Uri, String, Long> {

        protected void onProgressUpdate(String... progress) {
            pdialog.setMessage(progress[0]);
        }

        protected Long doInBackground(Uri... uris) {
            publishProgress(getString(R.string.beginning_download));
            try {
                URL url = new URL(uris[0].toString());
                File mossDir = new File("/sdcard/moss/");
                String defname = url.toString().replaceAll(".*/([^/]+)\\.mzip$", "$1");

                ConfigDatabase.Config config = new ConfigDatabase.Config();
                config.asset = false;
                config.name = defname;
                config.sourceUrl = url.toString();

                if (!mossDir.exists()) {
                    mossDir.mkdirs();
                }
                try  {
                    download(url, mossDir, config);

                    ConfigDatabase db = new ConfigDatabase(Configurations.this);
                    db.insertConfig(config);
                    configs = db.getConfigs();
                    db.close();

                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }

            } catch (MalformedURLException e) {
                Log.e(TAG, "", e);
            }
            updateHandler.sendEmptyMessage(-1);
            Configurations.this.pdialog.dismiss();
            return 100L;
        }

        private void download(URL url, File mossDir, ConfigDatabase.Config config) throws IOException {
            OutputStream os = null;

            ZipInputStream zis = 
                new ZipInputStream(
                    new BufferedInputStream((InputStream) url.getContent()));
            try {
                ZipEntry ze;
                while ((ze = zis.getNextEntry()) != null) {
                    publishProgress(
                            Configurations.this.getString(R.string.processing_x, ze.getName()));
                    if (ze.isDirectory()) {
                        new File(mossDir, ze.getName()).mkdirs();
                    } else {
                        File file = new File(mossDir, ze.getName());
                        writeFile(zis, file);
                        if (ze.getName().contains(MANIFEST) && file.exists()) {
                            parseManifest(Common.slurp(file), config);
                        } else if (ze.getName().contains(MOSSRC)) {
                            config.filepath = file.toString();
                        }
                    }
                }
            } finally {
                zis.close();
            }
        }

        private void writeFile(ZipInputStream zis, File dest) throws IOException {
            FileOutputStream os = null;
            try {
                int br;
                byte[] b = new byte[1024];
                os = new FileOutputStream(dest);
                while ((br = zis.read(b)) > 0) {
                    os.write(b, 0, br);
                }
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException ie) {
                    Log.e(TAG, "", ie);
                }
            }
        }

        /**
         * TODO: this is a pretty weak parser. Consider improvementing or
         * switching to Yaml or something.
         */
        private void parseManifest(String manifest, ConfigDatabase.Config config) {
            for (String line : manifest.split("\n")) {
                String[] arr = line.split(":");
                if (arr.length == 2) {
                    if ("name".equals(arr[0].trim())) {
                        config.name = arr[1].trim();
                    } else if ("description".equals(arr[0].trim())) {
                        config.desc = arr[1].trim();
                    }
                }
            }
        }
    }

    class ConfigAdapter extends ArrayAdapter<ConfigDatabase.Config> {

        public ConfigAdapter(Context context, List<ConfigDatabase.Config> configs) {
            super(context, R.layout.item_config, configs);
            this.configs = configs;
        }

        @Override
        public View getView(int position,
                            View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView =
                    inflater.inflate(R.layout.item_config, null, false);

                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(android.R.id.text1);
                holder.desc = (TextView) convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ConfigDatabase.Config config = configs.get(position);
            if (config == null) {
                /* Its a bummer man */
                Log.e("DeviceAdapter", "Config is null!");
                holder.name.setText(Configurations.this.getString(R.string.error_during_lookup));
                return convertView;
            }

            holder.name.setText(config.name);
            holder.desc.setText(config.desc);

            Context context = convertView.getContext();
            holder.name.setTextAppearance(context,
                    android.R.attr.textAppearanceLarge);
            holder.desc.setTextAppearance(context,
                    android.R.attr.textAppearanceSmall);

            return convertView;
        }

        class ViewHolder {
            private TextView name;
            private TextView desc;
        }

        private List<ConfigDatabase.Config> configs;
    }

    private static final String MANIFEST = "manifest.txt";
    private static final String MOSSRC = "mossrc";

    private ProgressDialog pdialog;
    private LayoutInflater inflater = null;
    private List <ConfigDatabase.Config> configs;
}
