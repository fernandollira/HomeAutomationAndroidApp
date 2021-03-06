package com.example.jesse.bedroomlight;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jesse on 1/22/17.
 */

public class DeviceReaderDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DeviceReader.db";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + DeviceReaderContract.FeedEntry.TABLE_NAME + " (" +
            DeviceReaderContract.FeedEntry.COLUMN_NAME_NAME + " TEXT PRIMARY KEY," +
            DeviceReaderContract.FeedEntry.COLUMN_NAME_TOPIC + " TEXT,"+
            DeviceReaderContract.FeedEntry.COLUMN_NAME_IMAGE + " BLOB)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + DeviceReaderContract.FeedEntry.TABLE_NAME;



    public DeviceReaderDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}