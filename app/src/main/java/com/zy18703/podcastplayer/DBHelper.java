package com.zy18703.podcastplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    // Help to control and manage the database

    // Table contain Hash value (to identify an URL) AND Comment
    static final String TABLE_NAME = "commentList";
    static final String COL_FIRST = "hash";
    static final String COL_SECOND = "comment";
    private static final String DATABASE_NAME = "comments";
    private static final String SQL_CREATION = "CREATE TABLE commentList (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_FIRST + " INTEGER, " +
            COL_SECOND + " VARCHAR(1024)" +
            ");";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
