package com.imapp.taskreminder.helper;

import android.database.Cursor;

import com.imapp.taskreminder.model.Task;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.DATE_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.DESC_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.NAME_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.STAT_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.TIME_TASK;

public class MappingHelper {
    public static ArrayList<Task> mapCursorToArrayList(Cursor cursor){
        ArrayList<Task> tasksList = new ArrayList<>();

        while (cursor.moveToNext()){
            String idTask = cursor.getString(cursor.getColumnIndexOrThrow(_ID));
            String titleTask = cursor.getString(cursor.getColumnIndexOrThrow(NAME_TASK));
            String dateTask = cursor.getString(cursor.getColumnIndexOrThrow(DATE_TASK));
            String timeTask = cursor.getString(cursor.getColumnIndexOrThrow(TIME_TASK));
            String descTask = cursor.getString(cursor.getColumnIndexOrThrow(DESC_TASK));
            String statTask = cursor.getString(cursor.getColumnIndexOrThrow(STAT_TASK));

            tasksList.add(new Task(idTask, titleTask, dateTask, timeTask, descTask, statTask));
        }
        return tasksList;
    }
}
