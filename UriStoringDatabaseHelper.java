package com.example.bookandpostroom;



import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class UriStoringDatabaseHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "imagesdb";
    private static final int DB_VERSION = 1;

    UriStoringDatabaseHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
       
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // handle upgrades
    }

    private void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IMAGES (_id TEXT PRIMARY KEY,Uri  TEXT)");
    }

    public void insertImage(SQLiteDatabase db, String id, String uri) {
        ContentValues imagevalues = new ContentValues();
        imagevalues.put("_id", id);
        imagevalues.put("Uri", uri);

        db.insert("IMAGES", null, imagevalues);
    }

}
