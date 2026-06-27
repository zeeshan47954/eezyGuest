package com.example.bookandpostroom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "rooms.db";
    private static final int DATABASE_VERSION = 1;

    // Table and columns
    public static final String TABLE_NAME = "rooms_table";
    public static final String COLUMN_FAVORITE = "favorite"; // 0 or 1
    public static final String COLUMN_ROOM = "room";         // room name

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_FAVORITE + " INTEGER CHECK(" + COLUMN_FAVORITE + " IN (0,1))," +
                COLUMN_ROOM + " TEXT PRIMARY KEY)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert new room
    public boolean insertRoom(int favorite, String roomName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FAVORITE, favorite);
        values.put(COLUMN_ROOM, roomName);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }
    public int isTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count > 0 ? 1 : 0;  // 1 if not empty, 0 if empty
    }

    // Update favorite status for a room
    public boolean updateFavorite(String roomName, int favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FAVORITE, favorite);

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ROOM + " = ?", new String[]{roomName});
        db.close();
        return rowsAffected > 0;
    }
    public boolean removeFavorite(String captionOrId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("rooms_table", "room = ?", new String[]{captionOrId});
        db.close();
        return rows > 0;
    }
    // Get all rooms
    public List<String> getAllRooms() {
        List<String> roomList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int favorite = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE));
            String room = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROOM));
            roomList.add("Room: " + room + " | Favorite: " + favorite);
        }
        cursor.close();
        db.close();
        return roomList;
    }

    // Get only favorite rooms
    public List<String> getFavoriteRooms() {
        List<String> favoriteRooms = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_FAVORITE + " = ?", new String[]{"1"}, null, null, null);

        while (cursor.moveToNext()) {
            String room = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROOM));
            favoriteRooms.add(room);
        }
        cursor.close();
        db.close();
        return favoriteRooms;
    }
    // Insert multiple rooms from a list
    // Insert multiple favorite rooms from a list




    // Delete a room
    public boolean deleteRoom(String roomName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, COLUMN_ROOM + " = ?", new String[]{roomName});
        db.close();
        return rowsDeleted > 0;
    }
}
