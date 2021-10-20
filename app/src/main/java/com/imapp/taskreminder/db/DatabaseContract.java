package com.imapp.taskreminder.db;

import android.provider.BaseColumns;

public class DatabaseContract {

    public static String TABLE_NAME = "Task";

    public static final class DbColumns implements BaseColumns {
        public static String ID_TASK = "id_task";
        public static String NAME_TASK = "title_task";
        public static String DATE_TASK = "date_task";
        public static String TIME_TASK = "time_task";
        public static String DESC_TASK = "desk_task";
        public static String STAT_TASK = "stat_task";
    }
}
