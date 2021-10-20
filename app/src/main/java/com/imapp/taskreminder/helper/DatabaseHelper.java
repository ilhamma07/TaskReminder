package com.imapp.taskreminder.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.imapp.taskreminder.db.DatabaseContract;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "dbTaskReminder";
    public static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE = String.format("CREATE TABLE %s" +
                    " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_NAME,
            DatabaseContract.DbColumns._ID,
            DatabaseContract.DbColumns.NAME_TASK,
            DatabaseContract.DbColumns.DATE_TASK,
            DatabaseContract.DbColumns.TIME_TASK,
            DatabaseContract.DbColumns.DESC_TASK,
            DatabaseContract.DbColumns.STAT_TASK);

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME);
        onCreate(db);
    }
}
