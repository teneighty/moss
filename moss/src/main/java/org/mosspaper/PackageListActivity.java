package org.mosspaper;

import android.app.ListActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipFile;
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
                    edit.putString("sample_config_file", c.confFile);
                    edit.putString("config_file", null);
                } else {
                    edit.putString("sample_config_file", "CUSTOM");
                    edit.putString("config_file", c.confFile);
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

            File mossDir = new File(Environment.getExternalStorageDirectory(), "moss");
            File confDir = new File(c.root);
            if (confDir.exists()) {
                /* Only delete if we are within the moss directory, otherwise its your problem */
                if (confDir.toString().indexOf(mossDir.toString()) == 0) {
                    publishProgress(
                        PackageListActivity.this.getString(R.string.removing_from_fs, confDir.toString()));
                    recursiveDelete(confDir);
                }
            }
            return 0L;
        }

        private void recursiveDelete(File fs) {
            if (null == fs || !fs.exists()) {
                return;
            }
            if (null == fs.listFiles()) {
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

    class PackLoaderTask extends AsyncTask<Uri, String, Boolean> {

        protected void onProgressUpdate(String... progress) {
            pdialog.setMessage(progress[0]);
        }

        protected void onPostExecute(Boolean result) {
            AlertDialog.Builder builder = 
                new AlertDialog.Builder(PackageListActivity.this);
            // builder.setTitle(getString(R.string.download_dialog);
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                    }
                });
            builder.setCancelable(true);
            if (result) {
                builder.setMessage(getString(R.string.download_finished));
            } else {
                builder.setMessage(getString(R.string.error_occured));
            }
            AlertDialog alert = builder.create();
            alert.show();

            updateHandler.sendEmptyMessage(-1);
            PackageListActivity.this.pdialog.dismiss();
        }

        protected Boolean doInBackground(Uri... uris) {
            publishProgress(getString(R.string.beginning_download));
            Uri uri = uris[0];
            String defname = null;
            String sourceUrl = null;
            try {
                if ("content".equals(uri.getScheme())) {
                    defname = "downloads";
                    sourceUrl = uri.toString();
                } else {
                    URL url = new URL(uri.toString());
                    defname = url.getHost() + "_" + 
                            url.toString().replaceAll(".*/([^/]+)\\." + EXT + "$", "$1");
                    sourceUrl = url.toString();
                }
                File mossDir = new File(Environment.getExternalStorageDirectory(), "moss");
                if (!mossDir.exists()) {
                    mossDir.mkdirs();
                }

                PackageDatabase.Package config = new PackageDatabase.Package();
                config.asset = false;
                config.name = defname;
                config.sourceUrl = sourceUrl;

                try {
                    fetchFile(uri, mossDir, config);

                    PackageDatabase db = new PackageDatabase(PackageListActivity.this);
                    db.storePackage(config);
                    packages = db.getPackages();
                    db.close();

                } catch (IOException e) {
                    Log.e(TAG, "", e);
                    return false;
                }

            } catch (MalformedURLException e) {
                Log.e(TAG, "", e);
                return false;
            }
            return true;
        }

        private void fetchFile(Uri uri, File mossDir, PackageDatabase.Package config) throws IOException, MalformedURLException {
            File zipFile = new File(mossDir, config.name + "." + EXT);
            File tmpDir = new File(mossDir, "tmp-" + config.name);

            BufferedInputStream dis = null;
            FileOutputStream os = null;
            try {
                if ("content".equals(uri.getScheme())) {
                    dis = new BufferedInputStream(
                                getContentResolver().openInputStream(uri));
                } else {
                    URL url = new URL(uri.toString());
                    dis = new BufferedInputStream((InputStream) url.getContent());
                }
                os = new FileOutputStream(zipFile);

                int br;
                byte[] b = new byte[1024];
                while ((br = dis.read(b)) > 0) {
                    os.write(b, 0, br);
                }
            } finally {
                if (null != dis) {
                    dis.close();
                }
                if (null != os) {
                    os.close();
                }
            }

            if (!zipFile.exists()) {
                return;
            }
            try {
                ZipFile zf = new ZipFile(zipFile);
                for (Enumeration e = zf.entries(); e.hasMoreElements();) {
                    ZipEntry ze = (ZipEntry) e.nextElement();
                    publishProgress(
                            PackageListActivity.this.getString(R.string.processing_x, ze.getName()));
                    if (ze.isDirectory()) {
                        new File(tmpDir, ze.getName()).mkdirs();
                    } else {
                        File file = new File(tmpDir, ze.getName());
                        writeFile(zf.getInputStream(ze), file);
                    }
                }
            } catch (IOException e) {
                throw e;
            } finally {
                zipFile.delete();
            }

            if (tmpDir.exists()) {
                File pakDir = new File(mossDir, config.name);
                tmpDir.renameTo(pakDir);
                config.root = pakDir.toString();
                buildConfig(config, pakDir);
            }
            if (null == config.confFile) {
                throw new IOException("Invalid package");
            }
        }

        private void buildConfig(PackageDatabase.Package config, File directory) throws IOException {
            for (File f : directory.listFiles()) {
                if (f.isDirectory()) {
                    buildConfig(config, f);
                } else {
                    if (f.getName().equals(MANIFEST) && f.exists()) {
                        parseManifest(Common.slurp(f), config);
                    } else if (f.getName().equals(MOSSRC)) {
                        config.confFile = f.toString();
                    }
                }
            }
        }

        private void writeFile(InputStream zis, File dest) throws IOException {
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
                holder.name = (TextView) convertView.findViewById(R.id.text1);
                holder.desc = (TextView) convertView.findViewById(R.id.text2);
                holder.url = (TextView) convertView.findViewById(R.id.url);
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
            holder.desc.setText(config.desc);

            if (null == config.sourceUrl || "".equals(config.sourceUrl.toString())) {
                config.sourceUrl = PackageListActivity.this.getString(R.string.local);
            } else {
                holder.url.setText(config.sourceUrl);
            }

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
            private TextView url;
        }

        private List<PackageDatabase.Package> packages;
    }

    private static final String MANIFEST = "manifest.txt";
    private static final String MOSSRC = "mossrc";
    private static final String EXT = "mpk";

    private ListView pkgList;
    private ProgressDialog pdialog;
    private LayoutInflater inflater = null;
    private List <PackageDatabase.Package> packages;
}
