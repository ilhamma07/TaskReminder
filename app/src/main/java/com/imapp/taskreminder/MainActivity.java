package com.imapp.taskreminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imapp.taskreminder.activity.AboutAppActivity;
import com.imapp.taskreminder.activity.AddTaskActivity;
import com.imapp.taskreminder.activity.AllTaskActivity;
import com.imapp.taskreminder.activity.ViewEditActivity;
import com.imapp.taskreminder.adapter.TaskAdapter;
import com.imapp.taskreminder.helper.MappingHelper;
import com.imapp.taskreminder.helper.TaskHelper;
import com.imapp.taskreminder.model.Task;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.DATE_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.DESC_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.NAME_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.STAT_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.TIME_TASK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoadTaskCallback {

    private Button btnAllTask, btnNewtask;
    private RecyclerView rvUpcomingTask;
    private TaskAdapter taskAdapter;
    private TaskHelper taskHelper;

    private TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyTextView = findViewById(R.id.text_empty_view);

        btnAllTask = findViewById(R.id.btn_all_task);
        btnNewtask = findViewById(R.id.btn_new_task);

        btnAllTask.setOnClickListener(this);
        btnNewtask.setOnClickListener(this);

        taskAdapter = new TaskAdapter(this);
        taskAdapter.notifyDataSetChanged();

        rvUpcomingTask = findViewById(R.id.rv_upcoming_task);
        rvUpcomingTask.setLayoutManager(new LinearLayoutManager(this));
        rvUpcomingTask.setHasFixedSize(true);
        rvUpcomingTask.setAdapter(taskAdapter);

        taskHelper = TaskHelper.getInstance(this);
        taskHelper.open();

        taskAdapter.setOnItemClickCallback(new TaskAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(Task data, int pos) {
                Intent intent = new Intent(MainActivity.this, ViewEditActivity.class);
                intent.putExtra(ViewEditActivity.EXTRA_POSITION, pos);
                intent.putExtra(ViewEditActivity.EXTRA_TASK, data);
                intent.putExtra(ViewEditActivity.EXTRA_STATS, data.getStatTask());
                startActivityForResult(intent, ViewEditActivity.REQUEST_UPDATE);
            }

            @Override
            public void onSwitchClicked(Task data, int pos) {
                if (data.getStatTask().equals("Done")){
                    setStat(data, pos, "Undone");
                } else if (data.getStatTask().equals("Undone")){
                    setStat(data, pos, "Done");
                }
            }
        });

        new LoadTaskAsync(taskHelper, this).execute();

        if (getSupportActionBar() != null ){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
            getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.colorPrimary)+"'>"+getResources().getString(R.string.app_name)+"</font>"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskHelper.open();
        new LoadTaskAsync(taskHelper, this).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskHelper.close();
    }

    private void setStat(Task task, int pos, String stats){
        taskHelper.open();
        String name_task = task.getTitleTask();
        String date_task = task.getDateTask();
        String time_task = task.getTimeTask();
        String desc_task = task.getDescTask();
        String stat_task = stats;

        task.setTitleTask(name_task);
        task.setDateTask(date_task);
        task.setTimeTask(time_task);
        task.setDescTask(desc_task);
        task.setStatTask(stat_task);

        ContentValues val = new ContentValues();
        val.put(NAME_TASK, name_task);
        val.put(DATE_TASK, date_task);
        val.put(TIME_TASK, time_task);
        val.put(DESC_TASK, desc_task);
        val.put(STAT_TASK, stat_task);

        long result = taskHelper.updateTask(task.getIdTask(),val);
        if (result > 0){
            Toast.makeText(this, "Statuss tugas "+task.getTitleTask()+" diubah", Toast.LENGTH_SHORT).show();
            taskAdapter.notifyItemChanged(pos);
        } else {
            Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (requestCode == AddTaskActivity.REQUEST_ADD){
                if (resultCode == AddTaskActivity.RESULT_ADD){
                    Task task = data.getParcelableExtra(AddTaskActivity.EXTRA_TASK);

                    taskAdapter.addData(task);
                    rvUpcomingTask.smoothScrollToPosition(taskAdapter.getItemCount() - 1);
                }
            } else if (requestCode == ViewEditActivity.REQUEST_UPDATE) {
                if (resultCode == ViewEditActivity.RESULT_UPDATE){
                    Task task = data.getParcelableExtra(ViewEditActivity.EXTRA_TASK);
                    int position = data.getIntExtra(ViewEditActivity.EXTRA_POSITION, 0);

                    taskAdapter.updateItem(position, task);
                    rvUpcomingTask.smoothScrollToPosition(position);
                } else if (resultCode == ViewEditActivity.RESULT_DELETE){
                    int position = data.getIntExtra(ViewEditActivity.EXTRA_POSITION, 0);

                    taskAdapter.removeData(position);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_all_task :
                Intent iAlltask = new Intent(this, AllTaskActivity.class);
                startActivity(iAlltask);
                break;

            case R.id.btn_new_task :
                Intent iNewTask = new Intent(this, AddTaskActivity.class);
                startActivityForResult(iNewTask, AddTaskActivity.REQUEST_ADD);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_about_app){
            Intent iAbout = new Intent(MainActivity.this, AboutAppActivity.class);
            startActivity(iAbout);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void preExecute() {

    }

    @Override
    public void postExecute(ArrayList<Task> tasks) {
        if (tasks.size() > 0){
            taskAdapter.clearData();
            for (int i = 0; i < tasks.size(); i++){
                String dateArray[] = tasks.get(i).getDateTask().split("-");
                int day = Integer.parseInt(dateArray[2]) - getCurrentDate();
                if (day >= 0 && day <=2 ){
                    taskAdapter.addData(tasks.get(i));
                }
            }
            if (taskAdapter.getItemCount() > 0){
                emptyTextView.setVisibility(View.GONE);
            } else {
                emptyTextView.setVisibility(View.VISIBLE);
            }
        } else {
            taskAdapter.setDataAll(new ArrayList<Task>());
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }

    private static class LoadTaskAsync extends AsyncTask<Void, Void, ArrayList<Task>> {
        private final WeakReference<TaskHelper> weakTaskHelper;
        private final WeakReference<LoadTaskCallback> weakCallback;

        private LoadTaskAsync(TaskHelper taskHelper, LoadTaskCallback callback) {
            weakTaskHelper = new WeakReference<>(taskHelper);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<Task> doInBackground(Void... voids) {
            Cursor dataCursor = weakTaskHelper.get().queryTaskAll();
            return MappingHelper.mapCursorToArrayList(dataCursor);
        }

        @Override
        protected void onPostExecute(ArrayList<Task> tasks) {
            super.onPostExecute(tasks);
            weakCallback.get().postExecute(tasks);
        }
    }

    private int getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        Date date = new Date();

        return Integer.parseInt(dateFormat.format(date));
    }
}

interface LoadTaskCallback {
    void preExecute();
    void postExecute(ArrayList<Task> tasks);
}