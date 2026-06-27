package com.example.bookandpostroom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ActivityCacheHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "activity_cache.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "last_activity";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ACTIVITY_NAME = "activity_name";

    public ActivityCacheHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +  // Ensures only one row
                COLUMN_ACTIVITY_NAME + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void saveLastActivity(String activityName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, 1); // Force ID to always be 1
        values.put(COLUMN_ACTIVITY_NAME, activityName);

        // Replaces the existing row if it exists (ensures only 1 row is stored)
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public String getLastActivity() {
        SQLiteDatabase db = this.getReadableDatabase();
        String activityName = null;
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ACTIVITY_NAME + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=1", null);

        if (cursor.moveToFirst()) {
            activityName = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return activityName;
    }
}


