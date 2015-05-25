package org.mosspaper;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import java.util.List;
import java.util.LinkedList;

public class PackageDatabase extends SQLiteOpenHelper {

    public static class Package {
        public long id;
        public String name;
        public String desc;
        public boolean asset;
        public String confFile;
        public String sourceUrl;
        public String root;
    }


    public PackageDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        getWritableDatabase().close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =
            "CREATE TABLE " + TABLE_NAME
                + " (_id INTEGER PRIMARY KEY, "
                + FIELD_NAME + " TEXT, "
                + FIELD_DESC + " TEXT, "
                + FIELD_CONFIG_PATH + " TEXT, "
                + FIELD_SOURCE_URL + " TEXT, "
                + FIELD_PACKAGE_ROOT + " TEXT, "
                + FIELD_IS_ASSET + " INTEGER ) ";
        db.execSQL(sql);

        defaultDatabase(db);
    }

    private void defaultDatabase(SQLiteDatabase db) {
        Package c1 = new Package();
        c1.name = mContext.getString(R.string.config_basic_name);
        c1.desc = mContext.getString(R.string.config_basic_desc);
        c1.confFile = "default.conf";
        c1.asset = true;
        storePackage(db, c1);

        Package c2 = new Package();
        c2.name = mContext.getString(R.string.config_network);
        c2.desc = mContext.getString(R.string.config_network_desc);
        c2.confFile = "network.conf";
        c2.asset = true;
        storePackage(db, c2);

        Package c3 = new Package();
        c3.name = mContext.getString(R.string.config_process);
        c3.desc = mContext.getString(R.string.config_process_desc);
        c3.confFile = "process.conf";
        c3.asset = true;
        storePackage(db, c3);

        Package c4 = new Package();
        c4.name = mContext.getString(R.string.config_full);
        c4.desc = mContext.getString(R.string.config_full_desc);
        c4.confFile = "full.conf";
        c4.asset = true;
        storePackage(db, c4);
    }

    @Override
    public final void onUpgrade(SQLiteDatabase db,
                                int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            Package ebuprof = new Package();
            ebuprof.name = mContext.getString(R.string.config_ebuprof);
            ebuprof.desc = mContext.getString(R.string.config_ebuprof_desc);
            ebuprof.confFile = "ebuprof.conf";
            ebuprof.asset = true;
            storePackage(db, ebuprof);
        }
    }

    public void storePackage(Package config) {
        synchronized (DB_LOCK) {
            SQLiteDatabase db = this.getWritableDatabase();
            storePackage(db, config);
        }
    }

    private void storePackage(SQLiteDatabase db, Package config) {
        Cursor c =
            db.query(TABLE_NAME, null,
                    " name = ? AND source_url = ? ",
                    new String[] { String.valueOf(config.name), String.valueOf(config.sourceUrl) },
                    null, null, null);
        if (c.moveToNext()) {
            config.id = c.getLong(c.getColumnIndexOrThrow("_id"));
            updatePackage(db, config);
        } else {
            insertPackage(db, config);
        }
        c.close();
    }


    public void insertPackage(Package config) {
        synchronized (DB_LOCK) {
            SQLiteDatabase db = this.getWritableDatabase();
            insertPackage(db, config);
        }
    }

    private void insertPackage(SQLiteDatabase db, Package config) {
        ContentValues values = buildContentValues(config);
        config.id = db.insert(TABLE_NAME, null, values);
    }

    public void updatePackage(Package config) {
        synchronized (DB_LOCK) {
            SQLiteDatabase db = this.getWritableDatabase();
            updatePackage(db, config);
        }
    }

    private void updatePackage(SQLiteDatabase db, Package config) {
        ContentValues values = buildContentValues(config);
        db.update(TABLE_NAME, values,
                "_id = ?", new String[] { String.valueOf(config.id) });
    }

    private ContentValues buildContentValues(Package config) {
        ContentValues values = new ContentValues();
        values.put(FIELD_NAME, config.name);
        values.put(FIELD_DESC, config.desc);
        values.put(FIELD_CONFIG_PATH, config.confFile);
        values.put(FIELD_PACKAGE_ROOT, config.root);
        values.put(FIELD_IS_ASSET, Boolean.toString(config.asset));
        values.put(FIELD_SOURCE_URL, config.sourceUrl);
        values.put(FIELD_PACKAGE_ROOT, config.root);
        return values;
    }

    public void deletePackage(Package config) {
        synchronized (DB_LOCK) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME, "_id = ?",
                    new String[] {String.valueOf(config.id)});


        }
    }

    public List<Package> getPackages() {
        List<Package> configs;

        synchronized (DB_LOCK) {
            SQLiteDatabase db = this.getReadableDatabase();

            /* Return package list ordered by name case insensitive */
            Cursor c =
                db.query(TABLE_NAME, null, null, null, null, null, "lower(name)", null);
            configs = createPackage(c);
            c.close();
        }

        return configs;
    }

    private List<Package> createPackage(Cursor c) {
        List<Package> configs = new LinkedList<Package>();

        final int COL_ID = c.getColumnIndexOrThrow("_id"),
            COL_NAME = c.getColumnIndexOrThrow(FIELD_NAME),
            COL_DESC = c.getColumnIndexOrThrow(FIELD_DESC),
            COL_PATH = c.getColumnIndexOrThrow(FIELD_CONFIG_PATH),
            COL_URL = c.getColumnIndexOrThrow(FIELD_SOURCE_URL),
            COL_ROOT = c.getColumnIndexOrThrow(FIELD_PACKAGE_ROOT),
            COL_ASSET = c.getColumnIndexOrThrow(FIELD_IS_ASSET);

        while (c.moveToNext()) {
            Package config = new Package();
            config.id = c.getLong(COL_ID);
            config.name = c.getString(COL_NAME);
            config.desc = c.getString(COL_DESC);
            config.confFile = c.getString(COL_PATH);
            config.sourceUrl = c.getString(COL_URL);
            config.root = c.getString(COL_ROOT);
            config.asset = Boolean.valueOf(c.getString(COL_ASSET));
            configs.add(config);
        }

        return configs;
    }

    private Context mContext;

    static final String DB_NAME = "configurations";
    static final int DB_VERSION = 2;

    static final String TABLE_NAME = "configurations";

    static final String FIELD_NAME = "name";
    static final String FIELD_DESC = "desc";
    static final String FIELD_CONFIG_PATH = "config_path";
    static final String FIELD_SOURCE_URL = "source_url";
    static final String FIELD_PACKAGE_ROOT = "package_root";
    static final String FIELD_IS_ASSET = "asset";

    static final Object[] DB_LOCK = new Object[0];

}
