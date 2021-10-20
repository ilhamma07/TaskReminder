package com.imapp.taskreminder.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.DATE_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.ID_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.TABLE_NAME;

public class TaskHelper {

    private static final String DATABASE_TABLE = TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static TaskHelper INSTANCE;
    private static SQLiteDatabase database;

    private TaskHelper(Context context){
        databaseHelper = new DatabaseHelper(context);
    }

    public static TaskHelper getInstance(Context context){
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TaskHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close(){
        databaseHelper.close();

        if (database.isOpen()){
            database.close();
        }
    }

    public Cursor queryTaskAll(){
        return database.query(
                DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                DATE_TASK + " DESC");
    }

    public long insertTask(ContentValues values){
        return database.insert(DATABASE_TABLE, null, values);
    }

    public int updateTask(String id, ContentValues values){
        return database.update(DATABASE_TABLE, values, _ID + " = ?", new String[]{id});
    }

    public int deleteTaskById(String id){
        return database.delete(DATABASE_TABLE, _ID + " = ?",new String[]{id});
    }
}
