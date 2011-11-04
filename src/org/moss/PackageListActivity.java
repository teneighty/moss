package org.moss;

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
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.text.Html;
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

public class PackageListActivity extends ListActivity {

    static final String TAG = "PackageListActivity";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.act_packages);

        this.pkgList = this.getListView();
        this.registerForContextMenu(pkgList);

        handleIntents();
        updateList();

        this.inflater = LayoutInflater.from(this);
        pkgList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v,
                                    int position, long id) {
                if (position >= packages.size()) {
                    /* TODO: report error */
                    return;
                }
                PackageDatabase.Package c = packages.get(position);
                startReloadTask(c);
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

        final PackageDatabase.Package config =
            (PackageDatabase.Package) pkgList.getItemAtPosition(info.position);;

        MenuItem remove = menu.add(R.string.remove);
        remove.setEnabled(!config.asset);
        remove.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                startRemoveTask(config);
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
        PackageDatabase db = new PackageDatabase(this);
        packages = db.getPackages();
        db.close();

        pkgList.setAdapter(
                new ConfigAdapter(this, packages));

    }

    protected Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            PackageListActivity.this.updateList();
        }
    };

    private ProgressDialog createDialog() {
        pdialog = new ProgressDialog(this);
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setMessage(getString(R.string.loading));
        pdialog.setCancelable(false);
        return pdialog;
    }

    private void startReloadTask(PackageDatabase.Package c) {
        pdialog = createDialog();
        if (null != c) {
            pdialog.setMessage(PackageListActivity.this.getString(R.string.switching_to, c.name));
        } else {
        }
        pdialog.show();

        new ReloadTask().execute(c);
    }

    class ReloadTask extends AsyncTask<PackageDatabase.Package, String, Long> {

        protected Long doInBackground(PackageDatabase.Package... packages) {
            if (null != packages && packages.length == 1) {
                PackageDatabase.Package c = packages[0];

                SharedPreferences prefs =
                    PackageListActivity.this.getSharedPreferences(WallPaper.SHARED_PREFS_NAME, 0);
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
            }
            Env.reload(PackageListActivity.this);

            return 0L;
        }

        protected void onPostExecute(Long result) {
            updateHandler.sendEmptyMessage(-1);
            PackageListActivity.this.pdialog.dismiss();
        }
    }

    private void startRemoveTask(PackageDatabase.Package config) {
        pdialog = createDialog();
        pdialog.show();

        new RemoveTask().execute(config);

    }

    class RemoveTask extends AsyncTask<PackageDatabase.Package, String, Long> {

        protected void onProgressUpdate(String... progress) {
            pdialog.setMessage(progress[0]);
        }

        protected Long doInBackground(PackageDatabase.Package... packages) {
            PackageDatabase.Package c = packages[0];

            publishProgress(
                PackageListActivity.this.getString(R.string.removing_from_db, c.name));
            PackageDatabase db = new PackageDatabase(PackageListActivity.this);
            db.deletePackage(c);
            db.close();

            File confFile = new File(c.filepath);
            if (confFile.exists()) {
                File confDir = confFile.getParentFile();
                publishProgress(
                    PackageListActivity.this.getString(R.string.removing_from_fs, confDir.toString()));
                recursiveDelete(confDir);
            }
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
            updateHandler.sendEmptyMessage(-1);
            PackageListActivity.this.pdialog.dismiss();
        }
    }

    class PackLoaderTask extends AsyncTask<Uri, String, Long> {

        protected void onProgressUpdate(String... progress) {
            pdialog.setMessage(progress[0]);
        }

        protected void onPostExecute(Long result) {
            updateHandler.sendEmptyMessage(-1);
            PackageListActivity.this.pdialog.dismiss();
        }

        protected Long doInBackground(Uri... uris) {
            publishProgress(getString(R.string.beginning_download));
            try {
                URL url = new URL(uris[0].toString());
                File mossDir = new File("/sdcard/moss/");
                String defname = url.toString().replaceAll(".*/([^/]+)\\.mzip$", "$1");

                PackageDatabase.Package config = new PackageDatabase.Package();
                config.asset = false;
                config.name = defname;
                config.sourceUrl = url.toString();

                if (!mossDir.exists()) {
                    mossDir.mkdirs();
                }
                try  {
                    download(url, mossDir, config);

                    PackageDatabase db = new PackageDatabase(PackageListActivity.this);
                    db.storePackage(config);
                    packages = db.getPackages();
                    db.close();

                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }

            } catch (MalformedURLException e) {
                Log.e(TAG, "", e);
            }
            return 100L;
        }

        private void download(URL url, File mossDir, PackageDatabase.Package config) throws IOException {
            OutputStream os = null;

            ZipInputStream zis = 
                new ZipInputStream(
                    new BufferedInputStream((InputStream) url.getContent()));
            try {
                ZipEntry ze;
                while ((ze = zis.getNextEntry()) != null) {
                    publishProgress(
                            PackageListActivity.this.getString(R.string.processing_x, ze.getName()));
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
        private void parseManifest(String manifest, PackageDatabase.Package config) {
            int state = 0;
            StringBuffer key = new StringBuffer("");
            StringBuffer value = new StringBuffer("");
            for (int i = 0; i < manifest.length(); ++i) {
                char c = manifest.charAt(i);
                if (0 == state) {
                    if (c == ':') {
                        state = 1;
                    } else {
                        key.append(c);
                    }
                } else if (1 == state) {
                    if ('\n' == c) {
                        String k = key.toString().trim();
                        if ("name".equals(k)) {
                            config.name = value.toString().trim();
                        } else if ("description".equals(k)) {
                            config.desc = value.toString().trim();
                        }
                        state = 0;
                        key = new StringBuffer("");
                        value = new StringBuffer("");
                    } else {
                        value.append(c);
                    }
                }
            }
        }
    }

    class ConfigAdapter extends ArrayAdapter<PackageDatabase.Package> {

        public ConfigAdapter(Context context, List<PackageDatabase.Package> packages) {
            super(context, R.layout.item_config, packages);
            this.packages = packages;
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

            PackageDatabase.Package config = packages.get(position);
            if (config == null) {
                /* Its a bummer man */
                Log.e("DeviceAdapter", "Config is null!");
                holder.name.setText(PackageListActivity.this.getString(R.string.error_during_lookup));
                return convertView;
            }

            holder.name.setText(config.name);
            holder.desc.setText(Html.fromHtml(config.desc));

            Context context = convertView.getContext();
            holder.name.setTextAppearance(context,
                    android.R.attr.textAppearanceLarge);
            holder.desc.setTextAppearance(context,
                    android.R.attr.textAppearanceSmall);

            if (true) {
                convertView.setBackgroundColor(R.color.selected_color);
            }

            return convertView;
        }

        class ViewHolder {
            private TextView name;
            private TextView desc;
        }

        private List<PackageDatabase.Package> packages;
    }

    private static final String MANIFEST = "manifest.txt";
    private static final String MOSSRC = "mossrc";

    private ListView pkgList;
    private ProgressDialog pdialog;
    private LayoutInflater inflater = null;
    private List <PackageDatabase.Package> packages;
}
