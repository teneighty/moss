package org.moss;

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
        public String sourceUrl;
        public boolean asset;
        public String filepath;
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
                + FIELD_IS_ASSET + " INTEGER ) ";
        db.execSQL(sql);

        Package c1 = new Package();
        c1.name = mContext.getString(R.string.config_basic_name);
        c1.desc = mContext.getString(R.string.config_basic_desc);
        c1.filepath = "default.conf";
        c1.asset = true;
        insertPackage(db, c1);

        Package c2 = new Package();
        c2.name = mContext.getString(R.string.config_network);
        c2.desc = mContext.getString(R.string.config_network_desc);
        c2.filepath = "network.conf";
        c2.asset = true;
        insertPackage(db, c2);

        Package c3 = new Package();
        c3.name = mContext.getString(R.string.config_process);
        c3.desc = mContext.getString(R.string.config_process_desc);
        c3.filepath = "process.conf";
        c3.asset = true;
        insertPackage(db, c3);
    }

    @Override
    public final void onUpgrade(SQLiteDatabase db,
                                int oldVersion, int newVersion) { }

    public void storePackage(Package config) {
        synchronized (DB_LOCK) {
            SQLiteDatabase db = this.getWritableDatabase();
            storePackage(db, config);
        }
    }

    private void storePackage(SQLiteDatabase db, Package config) {
        Cursor c =
            db.query(TABLE_NAME, null, 
                    " name = ? ", new String[] { String.valueOf(config.name) }, 
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
        values.put(FIELD_CONFIG_PATH, config.filepath);
        values.put(FIELD_SOURCE_URL, config.sourceUrl);
        values.put(FIELD_IS_ASSET, Boolean.toString(config.asset));
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

            Cursor c =
                db.query(TABLE_NAME, null, null, null, null, null, "name", null);
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
            COL_ASSET = c.getColumnIndexOrThrow(FIELD_IS_ASSET);

        while (c.moveToNext()) {
            Package config = new Package();
            config.id = c.getLong(COL_ID);
            config.name = c.getString(COL_NAME);
            config.desc = c.getString(COL_DESC);
            config.filepath = c.getString(COL_PATH);
            config.asset = Boolean.valueOf(c.getString(COL_ASSET));
            configs.add(config);
        }

        return configs;
    }

    private Context mContext;

    static final String DB_NAME = "configurations";
    static final int DB_VERSION = 1;

    static final String TABLE_NAME = "configurations";

    static final String FIELD_NAME = "name";
    static final String FIELD_DESC = "desc";
    static final String FIELD_CONFIG_PATH = "config_path";
    static final String FIELD_SOURCE_URL = "source_url";
    static final String FIELD_IS_ASSET = "asset";

    static final Object[] DB_LOCK = new Object[0];

}
