package com.practice.solarcalculator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.practice.solarcalculator.db.model.RecentLocation;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "locationManager";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String TABLE_NAME = "RecentLocation";

    private final Object lock = new Object();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUERY = "CREATE TABLE "
                + TABLE_NAME
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT"
                + ")";
        db.execSQL(CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DELETE_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(DELETE_QUERY);

        onCreate(db);
    }

    public long addLocation(RecentLocation location) {
        long row;
        synchronized (lock) {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, location.getLocationName());
            values.put(KEY_LATITUDE, location.getLatitude());
            values.put(KEY_LONGITUDE, location.getLongitude());

            row = this.getWritableDatabase().insert(TABLE_NAME, null, values);
            this.getWritableDatabase().close();
        }
        return row;
    }

    public List<RecentLocation> getAllLocations() {
        List<RecentLocation> list = new ArrayList<>();
        synchronized (lock) {
            String[] projection = {
              KEY_ID,
              KEY_NAME,
              KEY_LATITUDE,
              KEY_LONGITUDE
            };
            Cursor cursor = this.getReadableDatabase().query(TABLE_NAME,
                    projection, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(getData(cursor));
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return list;
    }

    private RecentLocation getData(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
        double latitude = cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE));
        double longitude = cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE));
        return new RecentLocation(name, latitude, longitude);
    }
}
